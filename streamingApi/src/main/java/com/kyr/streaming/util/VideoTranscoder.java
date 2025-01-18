package com.kyr.streaming.util;

import com.kyr.streaming.model.ProcessingProgress;
import com.kyr.streaming.model.Video;
import com.kyr.streaming.websocket.WebSocketHandlerDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class VideoTranscoder {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private MediaConvertClient mediaConvertClient;

    @Value(value = "${aws.mediaConvert.role.arn}")
    private String mediaConvertRoleARN;

    public String tanscodeVideo(String sourceFileUrl, String destFileUrl, int width, int height,
                                       int bitRate) {
        try {
            DescribeEndpointsResponse res = mediaConvertClient
                    .describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

            if (res.endpoints().size() <= 0) {
                log.error("Cannot find MediaConvert service endpoint URL!");
                throw AwsServiceException.create("Cannot find MediaConvert service endpoint URL!", null);
            }
            String endpointURL = res.endpoints().get(0).url();
            log.info("MediaConvert service URL: {}", endpointURL);
            log.info("MediaConvert role arn: {}", mediaConvertRoleARN);
            log.info("MediaConvert input file: {}", sourceFileUrl);
            log.info("MediaConvert output path: {}", destFileUrl);

            MediaConvertClient emc = MediaConvertClient.builder()
                    .endpointOverride(URI.create(endpointURL))
                    .build();

            // output group Preset HLS profile
            Output hls = createOutput("_hls", "_$dt$", bitRate, 8, height, width);

            OutputGroup appleHLS = OutputGroup.builder().name("Apple HLS").customName("Apple HLS")
                    .outputGroupSettings(OutputGroupSettings.builder()
                            .type(OutputGroupType.HLS_GROUP_SETTINGS)
                            .hlsGroupSettings(HlsGroupSettings.builder()
                                    .directoryStructure(
                                            HlsDirectoryStructure.SINGLE_DIRECTORY)
                                    .manifestDurationFormat(
                                            HlsManifestDurationFormat.INTEGER)
                                    .streamInfResolution(
                                            HlsStreamInfResolution.INCLUDE)
                                    .clientCache(HlsClientCache.ENABLED)
                                    .captionLanguageSetting(
                                            HlsCaptionLanguageSetting.OMIT)
                                    .manifestCompression(
                                            HlsManifestCompression.NONE)
                                    .codecSpecification(
                                            HlsCodecSpecification.RFC_4281)
                                    .outputSelection(
                                            HlsOutputSelection.MANIFESTS_AND_SEGMENTS)
                                    .programDateTime(HlsProgramDateTime.EXCLUDE)
                                    .programDateTimePeriod(600)
                                    .timedMetadataId3Frame(
                                            HlsTimedMetadataId3Frame.PRIV)
                                    .timedMetadataId3Period(10)
                                    .destination(destFileUrl)
                                    .segmentControl(HlsSegmentControl.SEGMENTED_FILES)
                                    .minFinalSegmentLength((double) 0)
                                    .segmentLength(4).minSegmentLength(0).build())
                            .build())
                    .outputs(hls).build();

            OutputGroup fileMp4 = OutputGroup.builder().name("File Group").customName("mp4")
                    .outputGroupSettings(OutputGroupSettings.builder()
                            .type(OutputGroupType.FILE_GROUP_SETTINGS)
                            .fileGroupSettings(FileGroupSettings.builder()
                                    .destination(destFileUrl).build())
                            .build())
                    .outputs(Output.builder().extension("mp4")
                            .containerSettings(ContainerSettings.builder()
                                    .container(ContainerType.MP4).build())
                            .videoDescription(VideoDescription.builder().width(width)
                                    .height(height)
                                    .scalingBehavior(ScalingBehavior.DEFAULT)
                                    .sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(
                                            VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT)
                                    .respondToAfd(RespondToAfd.NONE)
                                    .afdSignaling(AfdSignaling.NONE)
                                    .dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder()
                                            .codec(VideoCodec.H_264)
                                            .h264Settings(H264Settings
                                                    .builder()
                                                    .rateControlMode(
                                                            H264RateControlMode.QVBR)
                                                    .parControl(H264ParControl.INITIALIZE_FROM_SOURCE)
                                                    .qualityTuningLevel(
                                                            H264QualityTuningLevel.SINGLE_PASS)
                                                    .qvbrSettings(
                                                            H264QvbrSettings.builder()
                                                                    .qvbrQualityLevel(
                                                                            8)
                                                                    .build())
                                                    .codecLevel(H264CodecLevel.AUTO)
                                                    .codecProfile(H264CodecProfile.MAIN)
                                                    .maxBitrate(bitRate)
                                                    .framerateControl(
                                                            H264FramerateControl.INITIALIZE_FROM_SOURCE)
                                                    .gopSize(2.0)
                                                    .gopSizeUnits(H264GopSizeUnits.SECONDS)
                                                    .numberBFramesBetweenReferenceFrames(
                                                            2)
                                                    .gopClosedCadence(
                                                            1)
                                                    .gopBReference(H264GopBReference.DISABLED)
                                                    .slowPal(H264SlowPal.DISABLED)
                                                    .syntax(H264Syntax.DEFAULT)
                                                    .numberReferenceFrames(
                                                            3)
                                                    .dynamicSubGop(H264DynamicSubGop.STATIC)
                                                    .fieldEncoding(H264FieldEncoding.PAFF)
                                                    .sceneChangeDetect(
                                                            H264SceneChangeDetect.ENABLED)
                                                    .minIInterval(0)
                                                    .telecine(H264Telecine.NONE)
                                                    .framerateConversionAlgorithm(
                                                            H264FramerateConversionAlgorithm.DUPLICATE_DROP)
                                                    .entropyEncoding(
                                                            H264EntropyEncoding.CABAC)
                                                    .slices(1)
                                                    .unregisteredSeiTimecode(
                                                            H264UnregisteredSeiTimecode.DISABLED)
                                                    .repeatPps(H264RepeatPps.DISABLED)
                                                    .adaptiveQuantization(
                                                            H264AdaptiveQuantization.HIGH)
                                                    .spatialAdaptiveQuantization(
                                                            H264SpatialAdaptiveQuantization.ENABLED)
                                                    .temporalAdaptiveQuantization(
                                                            H264TemporalAdaptiveQuantization.ENABLED)
                                                    .flickerAdaptiveQuantization(
                                                            H264FlickerAdaptiveQuantization.DISABLED)
                                                    .softness(0)
                                                    .interlaceMode(H264InterlaceMode.PROGRESSIVE)
                                                    .build())
                                            .build())
                                    .build())
                            .audioDescriptions(AudioDescription.builder()
                                    .audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                                    .languageCodeControl(
                                            AudioLanguageCodeControl.FOLLOW_INPUT)
                                    .codecSettings(AudioCodecSettings.builder()
                                            .codec(AudioCodec.AAC)
                                            .aacSettings(AacSettings
                                                    .builder()
                                                    .codecProfile(AacCodecProfile.LC)
                                                    .rateControlMode(
                                                            AacRateControlMode.CBR)
                                                    .codingMode(AacCodingMode.CODING_MODE_2_0)
                                                    .sampleRate(44100)
                                                    .bitrate(160000)
                                                    .rawFormat(AacRawFormat.NONE)
                                                    .specification(AacSpecification.MPEG4)
                                                    .audioDescriptionBroadcasterMix(
                                                            AacAudioDescriptionBroadcasterMix.NORMAL)
                                                    .build())
                                            .build())
                                    .build())
                            .build())
                    .build();


            Map<String, AudioSelector> audioSelectors = new HashMap<>();
            audioSelectors.put("Audio Selector 1",
                    AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT)
                            .offset(0).build());

            JobSettings jobSettings = JobSettings.builder().inputs(Input.builder()
                            .audioSelectors(audioSelectors)
                            .videoSelector(
                                    VideoSelector.builder().colorSpace(ColorSpace.FOLLOW)
                                            .rotate(InputRotate.DEGREE_0).build())
                            .filterEnable(InputFilterEnable.AUTO).filterStrength(0)
                            .deblockFilter(InputDeblockFilter.DISABLED)
                            .denoiseFilter(InputDenoiseFilter.DISABLED).psiControl(InputPsiControl.USE_PSI)
                            .timecodeSource(InputTimecodeSource.EMBEDDED).fileInput(sourceFileUrl).build())
                    .outputGroups(appleHLS, fileMp4).build();

            CreateJobRequest createJobRequest = CreateJobRequest.builder().role(mediaConvertRoleARN)
                    .settings(jobSettings)
                    .build();

            CreateJobResponse createJobResponse = emc.createJob(createJobRequest);
            return createJobResponse.job().id();

        } catch (MediaConvertException e) {
            log.error(e.getMessage());
            throw AwsServiceException.create(e.getMessage(), e);
        }

//        FFmpeg.atPath()
//                .addInput(UrlInput.fromUrl("videos/" + videoId + "/original" + fileExtention))
//                .setOverwriteOutput(true)
//                .addArguments("-movflags", "faststart")
//                .addOutput(UrlOutput.toUrl("videos/" + videoId + "/" + height + fileExtention).setFrameSize(width, height).setFrameRate(frameRate))
//                .setProgressListener(progress -> {
//                    try {
//                        double percents = 100.0 * progress.getTimeMillis() / duration.get();
//                        log.info("Progress {}p{}fps: {}", height, frameRate, percents);
//                        if (height.intValue() == 1080) {
//                            processingProgress.setFullHd(percents);
//                        }
//                        else if (height.intValue() == 720) {
//                            processingProgress.setHd(percents);
//                        }
//                        else if (height.intValue() == 480) {
//                            processingProgress.setSd(percents);
//                        }
//                        else if (height.intValue() == 360) {
//                            processingProgress.setLd(percents);
//                        }
//                        Gson gson = new Gson();
//                        webSocketHandlerDelegate.sendProgressUpdateToSession(videoId, gson.toJson(processingProgress));
//                    } catch(Exception ignored) {}
//                })
//                .execute();
    }

    public String createThumbnail(String sourceVideo, String destination) {
        try {
            DescribeEndpointsResponse res = mediaConvertClient
                    .describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

            if (res.endpoints().size() <= 0) {
                System.out.println("Cannot find MediaConvert service endpoint URL!");
                System.exit(1);
            }
            String endpointURL = res.endpoints().get(0).url();

            MediaConvertClient emc = MediaConvertClient.builder()
                    .endpointOverride(URI.create(endpointURL))
                    .build();

            OutputGroup thumbs = OutputGroup.builder().name("File Group").customName("thumbs")
                    .outputGroupSettings(OutputGroupSettings.builder()
                            .type(OutputGroupType.FILE_GROUP_SETTINGS)
                            .fileGroupSettings(FileGroupSettings.builder()
                                    .destination(destination).build())
                            .build())
                    .outputs(Output.builder().extension("jpg")
                            .containerSettings(ContainerSettings.builder()
                                    .container(ContainerType.RAW).build())
                            .videoDescription(VideoDescription.builder()
                                    .scalingBehavior(ScalingBehavior.DEFAULT)
                                    .sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(
                                            VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT)
                                    .dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder()
                                            .codec(VideoCodec.FRAME_CAPTURE)
                                            .frameCaptureSettings(
                                                    FrameCaptureSettings
                                                            .builder()
                                                            .framerateNumerator(
                                                                    1)
                                                            .framerateDenominator(
                                                                    1)
                                                            .maxCaptures(10000000)
                                                            .quality(80)
                                                            .build())
                                            .build())
                                    .build())
                            .build())
                    .build();

            JobSettings jobSettings = JobSettings.builder().inputs(Input.builder()
                            .videoSelector(
                                    VideoSelector.builder().colorSpace(ColorSpace.FOLLOW)
                                            .rotate(InputRotate.DEGREE_0).build())
                            .filterEnable(InputFilterEnable.AUTO).filterStrength(0)
                            .deblockFilter(InputDeblockFilter.DISABLED)
                            .denoiseFilter(InputDenoiseFilter.DISABLED).psiControl(InputPsiControl.USE_PSI)
                            .timecodeSource(InputTimecodeSource.EMBEDDED).fileInput(sourceVideo).build())
                    .outputGroups(thumbs).build();

            CreateJobRequest createJobRequest = CreateJobRequest.builder().role(mediaConvertRoleARN)
                    .settings(jobSettings)
                    .build();

            CreateJobResponse createJobResponse = emc.createJob(createJobRequest);
            return createJobResponse.job().id();

        } catch (MediaConvertException e) {
            log.error(e.getMessage());
            throw AwsServiceException.create(e.getMessage(), e);
        }
//        new File("videos/" + video.getId().toString()).mkdirs();
//        File tempInputFile = new File("videos/" + video.getId() + "/" + "original.mp4");
//
//        try (OutputStream os = new FileOutputStream(tempInputFile)) {
//            os.write(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        FFmpeg.atPath()
//                .addArguments("-ss", "00:00:01")
//                .addInput(UrlInput.fromUrl("videos/" + video.getId() + "/" + "original.mp4"))
//                .setOverwriteOutput(true)
//                .addArguments("-frames:v", "1")
//                .addOutput(UrlOutput.toUrl("videos/" + video.getId() + "/" + "thumbnail" + ".png").setFrameSize(1920, 1080))
//                .execute();
//
//        tempInputFile.delete();
    }

    private final static Output createOutput(String nameModifier,
                                             String segmentModifier,
                                             int qvbrMaxBitrate,
                                             int qvbrQualityLevel,
                                             int targetHeight,
                                             int targetWidth) {

        Output output = null;
        try {
            output = Output.builder().nameModifier(nameModifier).outputSettings(OutputSettings.builder()
                            .hlsSettings(HlsSettings.builder().segmentModifier(segmentModifier)
                                    .audioGroupId("program_audio")
                                    .iFrameOnlyManifest(HlsIFrameOnlyManifest.EXCLUDE).build())
                            .build())
                    .containerSettings(ContainerSettings.builder().container(ContainerType.M3_U8)
                            .m3u8Settings(M3u8Settings.builder().audioFramesPerPes(4)
                                    .pcrControl(M3u8PcrControl.PCR_EVERY_PES_PACKET)
                                    .pmtPid(480).privateMetadataPid(503)
                                    .programNumber(1).patInterval(0).pmtInterval(0)
                                    .scte35Source(M3u8Scte35Source.NONE)
                                    .scte35Pid(500).nielsenId3(M3u8NielsenId3.NONE)
                                    .timedMetadata(TimedMetadata.NONE)
                                    .timedMetadataPid(502).videoPid(481)
                                    .audioPids(482, 483, 484, 485, 486, 487, 488,
                                            489, 490, 491, 492)
                                    .build())
                            .build())
                    .videoDescription(
                            VideoDescription.builder().width(targetWidth)
                                    .height(targetHeight)
                                    .scalingBehavior(ScalingBehavior.DEFAULT)
                                    .sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(
                                            VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT)
                                    .respondToAfd(RespondToAfd.NONE)
                                    .afdSignaling(AfdSignaling.NONE)
                                    .dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder()
                                            .codec(VideoCodec.H_264)
                                            .h264Settings(H264Settings
                                                    .builder()
                                                    .rateControlMode(
                                                            H264RateControlMode.QVBR)
                                                    .parControl(H264ParControl.INITIALIZE_FROM_SOURCE)
                                                    .qualityTuningLevel(
                                                            H264QualityTuningLevel.SINGLE_PASS)
                                                    .qvbrSettings(H264QvbrSettings
                                                            .builder()
                                                            .qvbrQualityLevel(
                                                                    qvbrQualityLevel)
                                                            .build())
                                                    .codecLevel(H264CodecLevel.AUTO)
                                                    .codecProfile((targetHeight > 720
                                                            && targetWidth > 1280)
                                                            ? H264CodecProfile.HIGH
                                                            : H264CodecProfile.MAIN)
                                                    .maxBitrate(qvbrMaxBitrate)
                                                    .framerateControl(
                                                            H264FramerateControl.INITIALIZE_FROM_SOURCE)
                                                    .gopSize(2.0)
                                                    .gopSizeUnits(H264GopSizeUnits.SECONDS)
                                                    .numberBFramesBetweenReferenceFrames(
                                                            2)
                                                    .gopClosedCadence(
                                                            1)
                                                    .gopBReference(H264GopBReference.DISABLED)
                                                    .slowPal(H264SlowPal.DISABLED)
                                                    .syntax(H264Syntax.DEFAULT)
                                                    .numberReferenceFrames(
                                                            3)
                                                    .dynamicSubGop(H264DynamicSubGop.STATIC)
                                                    .fieldEncoding(H264FieldEncoding.PAFF)
                                                    .sceneChangeDetect(
                                                            H264SceneChangeDetect.ENABLED)
                                                    .minIInterval(0)
                                                    .telecine(H264Telecine.NONE)
                                                    .framerateConversionAlgorithm(
                                                            H264FramerateConversionAlgorithm.DUPLICATE_DROP)
                                                    .entropyEncoding(
                                                            H264EntropyEncoding.CABAC)
                                                    .slices(1)
                                                    .unregisteredSeiTimecode(
                                                            H264UnregisteredSeiTimecode.DISABLED)
                                                    .repeatPps(H264RepeatPps.DISABLED)
                                                    .adaptiveQuantization(
                                                            H264AdaptiveQuantization.HIGH)
                                                    .spatialAdaptiveQuantization(
                                                            H264SpatialAdaptiveQuantization.ENABLED)
                                                    .temporalAdaptiveQuantization(
                                                            H264TemporalAdaptiveQuantization.ENABLED)
                                                    .flickerAdaptiveQuantization(
                                                            H264FlickerAdaptiveQuantization.DISABLED)
                                                    .softness(0)
                                                    .interlaceMode(H264InterlaceMode.PROGRESSIVE)
                                                    .build())
                                            .build())
                                    .build())
                    .audioDescriptions(AudioDescription.builder()
                            .audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                            .languageCodeControl(AudioLanguageCodeControl.FOLLOW_INPUT)
                            .codecSettings(AudioCodecSettings.builder()
                                    .codec(AudioCodec.AAC).aacSettings(AacSettings
                                            .builder()
                                            .codecProfile(AacCodecProfile.LC)
                                            .rateControlMode(
                                                    AacRateControlMode.CBR)
                                            .codingMode(AacCodingMode.CODING_MODE_2_0)
                                            .sampleRate(44100)
                                            .bitrate(96000)
                                            .rawFormat(AacRawFormat.NONE)
                                            .specification(AacSpecification.MPEG4)
                                            .audioDescriptionBroadcasterMix(
                                                    AacAudioDescriptionBroadcasterMix.NORMAL)
                                            .build())
                                    .build())
                            .build())
                    .build();
        } catch (MediaConvertException e) {
            log.error(e.getMessage());
        }
        return output;
    }
}
