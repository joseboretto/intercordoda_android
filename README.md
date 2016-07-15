# intercordoda_Android

En Agosto de 2014 (Segundo año de Ing. en Sistemas) como usuario de los colectivos de http://www.intercordoba.com.ar/ para viajar mensulamente, cada vez que viajaba necesitaba ver un excel con horarios que se descarga de su pagina web, como esto es bastante incomodo decidi aprender java para android y contruir un app que me sirva a mi y todo el que quiera, la puede descargar. Luego la empresa se intereso y agregamos algunas funcionalidades.


#Julio 2016
- Importar los 3 proyectos (El de intercordoba + 2 librerias), depencias
- Ir a propiedades > Android > Libreria > Eliminamos las que estan y las volvemos a agregar.
- Nos decargamos el API 22, porque a partir de la 23 "HttpClient is not supported any more in sdk 23. You have to use URLConnection or downgrade to sdk 22 (compile 'com.android.support:appcompat-v7:22.2.0')"
- Nos decargamos el API 19, para android-support-v7-appcompat
- Cambio el sistema de control, ahora se actualizan los horarios segun la fecha del arhivo online.
- Se deberia hacer re-ingenieria para aplicar patrones de diseno y UX.
