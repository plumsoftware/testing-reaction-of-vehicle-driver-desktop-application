Чтобы сделать исполняемый файл и сразу его запустить в терминале нужно набрать ```./gradlew :runDistributable```. Предпочтительней запускать именно эту команду.

Для создания образа исполняемого приложения нужна выполняить в терминале ```./gradlew :createDistributable```

Для сборки исполняемого файла для текущей ОС выполнять в терминале ```./gradlew :packageDistributionForCurrentOS```

Больше есть вот в этой документации https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md#basic-usage
