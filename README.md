
# Hermes Message Agregator

A brief description how to run this project.


## Features

- Receive requests with Message object in body
- Choose messenger (Telegram, WhatsApp) to send message according priority, existence of user or chat with him
- Send message using Telegram
- Send message using WhatsApp


## Run Locally

This project requires **JDK17**.

Before running please add this **VM option** in Run/Depug Configurations tab:

```bash
  -Djava.awt.headless=false
```



## Usage/Testing
For testing you can use Postman. First of all you need to login in yours WhatsApp and Telegram accounts. Next requests you need to send with Text body containing your phone number:

```text
380111111111
```

#### To login in Telegram send this POST request with body mentioned above:
```bash
http://localhost:8080/api/v1/messenger/telegram/login
```
In your console will be request for submit code sended to you. All subsequent starts of the application needs the same procedure, but without confirming login.



#### To login in WhatsApp send this POST request with body mentioned above:


```bash
http://localhost:8080/api/v1/messenger/whatsapp/login
```
QrCode will appear on you screen, scan it with your phone in WhatsApp app (connectred devices tab).
All subsequent starts of the application needs the same procedure, but without confirming login.


#### To send message create POST request:

```bash
http://localhost:8080/api/v1/message/send
```

with json body:

```json
{
    "phoneNumber": "380222222222",
    "text": "test"
}
```

