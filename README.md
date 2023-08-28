
# Hermes Message Agregator

This is backend for Hermes project.


## Features

- Receive requests with Message object in body
- Choose messenger (Telegram, WhatsApp) to send message according priority, existence of user or chat with him
- Send message using Telegram
- Send message using WhatsApp

## Requirements

- JDK `17.0.2`
- Postman
- Postgres `9.5.25`
- Maven

## Run Locally
Clone the project

```bash
  git clone https://sachukoleksandr@bitbucket.org/VGordejevs/hermes-backend.git
```
Open project with editor (Idea).

Before running please add this **VM option** in Run/Depug Configurations tab:

```bash
  -Djava.awt.headless=false
```



## Usage
As it is just backend part for using you need to start front
```http
https://sachukoleksandr@bitbucket.org/develSO/hermes-frontend.git
```
