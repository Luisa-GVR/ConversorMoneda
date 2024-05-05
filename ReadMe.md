Agregar la APIKEY correspondiente al inicio del programa en Main

fetchSupportedCurrencies sirve para obtener de la base de datos de la API las currencies actuales, en caso de solo querer valores predeterminados, configurar     static Set<String> supportedCurrencies = new HashSet<>(); al gusto.

updateConversionCounts y updateConversionToCounts sirve para mantener el top 3

conversor devuelve un JsonObject con el JSON del request, para usarlo, agregar el valor a convertir, a lo que se desea convertir, y la cantidad