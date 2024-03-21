<!DOCTYPE html>
<html>
<head>
    <title>Demo Login Page</title>
</head>
<body>
    <h2>Welcome to My Application</h2>
    <form action="${url.loginAction}" method="post">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username"><br>
        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="Login">
    </form>
</body>
</html>
