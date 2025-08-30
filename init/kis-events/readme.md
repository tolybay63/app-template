### Как задать собственные учетные данные для Telegram-бота

Чтобы бот добавлялся в БД (на этапе `env-reset`) нужно разместить в `init/kis-events` файл `bot-info.txt`, в котором
указаны `username` и `token` вашего Telegram-бота:

```
username: my_test_dev_bot
token: 7626491281:AACCmFXOoffg6K3VR2zk3rlibMk9rep0oSu
```

Пример - см. файл `bot-info.sample.txt`.

**Важно!** Свой файл `bot-info.txt` в репозитарий не класть, скрыть через `.gitignore`.