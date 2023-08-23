const ErrorPage = () => {
  const error = useRouteError();

  let title = "An error occured!";
  let message = "Something went wrong...";

  if (error.data.message) {
    message = error.data.message;
  }

  if (error.status === 404) {
    title = "Not found!";
    message = "Could not find resource or page";
  }

  return (
    <div className="text-center">
      <h1>{title}</h1>
      <p>{message}</p>
    </div>
  );
};

export default ErrorPage;
