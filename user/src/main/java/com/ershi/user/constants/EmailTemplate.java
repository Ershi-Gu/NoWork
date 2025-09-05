package com.ershi.user.constants;

/**
 * 邮件通知模板
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
public class EmailTemplate {

    public static final String REGISTER_EMAIL_CAPTCHA_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <title>注册验证码</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f4f4f7;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                        padding: 30px;
                    }
                    h1 {
                        color: #333333;
                        text-align: center;
                    }
                    p {
                        color: #555555;
                        font-size: 16px;
                        line-height: 1.6;
                    }
                    .captcha {
                        display: block;
                        width: fit-content;
                        margin: 20px auto;
                        font-size: 28px;
                        font-weight: bold;
                        color: #ffffff;
                        background-color: #4CAF50;
                        padding: 10px 20px;
                        border-radius: 5px;
                        letter-spacing: 4px;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #999999;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>欢迎注册挪窝！</h1>
                    <p>您好，感谢您注册挪窝。请使用以下验证码完成注册：</p>
                    <span class="captcha">%s</span>
                    <p>该验证码 5 分钟内有效，请尽快完成验证。如非本人操作，请忽略本邮件。</p>
                    <div class="footer">
                        © 2025 <a href="https://github.com/Ershi-Gu" target="_blank" style="color:#999999; text-decoration:none;">Ershi-Gu</a> 版权所有
                    </div>
                </div>
            </body>
            </html>
            """;
}
