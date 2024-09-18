<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Musicboxd - Login</title>
    <style>
        :root {
            --background-base: #121212;
            --background-highlight: #1a1a1a;
            --background-press: #000;
            --background-elevated-base: #242424;
            --background-elevated-highlight: #2a2a2a;
            --background-tinted-base: hsla(0,0%,100%,.07);
            --background-tinted-highlight: hsla(0,0%,100%,.1);
            --background-tinted-press: hsla(0,0%,100%,.04);
            --text-base: #fff;
            --text-subdued: #a7a7a7;
            --text-bright-accent: #1ed760;
            --essential-base: #fff;
            --essential-subdued: #727272;
            --essential-bright-accent: #1ed760;
            --decorative-base: #fff;
            --decorative-subdued: #292929;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Circular Std', Arial, sans-serif;
            background-color: var(--background-base);
            color: var(--text-base);
            line-height: 1.6;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background-color: var(--background-elevated-base);
            padding: 40px;
            border-radius: 8px;
            width: 100%;
            max-width: 400px;
        }

        h1 {
            font-size: 32px;
            text-align: center;
            margin-bottom: 24px;
            color: var(--text-bright-accent);
        }

        form {
            display: flex;
            flex-direction: column;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: var(--text-subdued);
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 10px;
            border: none;
            background-color: var(--background-tinted-base);
            color: var(--text-base);
            border-radius: 4px;
        }

        input[type="checkbox"] {
            margin-right: 8px;
        }

        .button {
            background-color: var(--essential-bright-accent);
            color: var(--background-base);
            border: none;
            padding: 12px;
            border-radius: 500px;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .button:hover {
            background-color: #1fdf64;
        }

        .register-link {
            text-align: center;
            margin-top: 16px;
        }

        .register-link a {
            color: var(--text-bright-accent);
            text-decoration: none;
        }

        .register-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Musicboxd</h1>
    <c:url var="loginUrl" value="/user/login" />
    <form action="${loginUrl}" method="post">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" name="remember_me">
                Remember me
            </label>
        </div>
        <button type="submit" class="button">Login</button>
    </form>
    <div class="register-link">
        <c:url var="registerUrl" value="/user/register" />
        <a href="${registerUrl}">Don't have an account yet? Register here</a>
    </div>
</div>
</body>
</html>
