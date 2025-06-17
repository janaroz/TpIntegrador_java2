#descarga la imagen del jdk
FROM eclipse-temurin:17.0.15_6-jdk
# amazoncorretto:17

#le asigno el puerto que voy a usar
EXPOSE 9090
# ES IFNROMATIVA. NO CONFIGURA EL PUERTO. EXPONE ESE PUERTO.

# DIRECTORIO. DEFINIR EL DURECTORIO DE RAIZ.
WORKDIR /root

#DESCARGAR TPDAS ÑAS DEPENDENCIAS
# copiar el pom y el maven embebido.
#carpetan de maven que se vana  copiar.  carpetas mvn.

COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

## le indico que dependencias quiero descargar y mave para uqe las gestione.

## para descargar uso el comando run
RUN ./mvnw dependency:go-offline

## busco la imagen del jdk, defino el puerto 8080, la carpeta que voy a trabajar, copio de als despenedencias y luego als descargo.. y leugo copio el codigo fuente al contenedor.
## ULTIMO COPIAR EL CIDIGO FUENTE.. PRIMERO LAS DEPENDENCIAS

# COPIAR EL CÓDIGO FUENTE
COPY ./src /root/src
#CARPETA SOUCE LA COMPIO DENTRO DEL ROOT AMBIEN DENTRO SRC DENTRO DEL CONTENEDOR.
#contruir o compilar la aplicacion.
RUN ./mvnw clean install -DskipTests

# necesito que la aplicacion inicie
# levanto la aplicacion
ENTRYPOINT ["java", "-jar", "root/target/TpIntegrador.jar"]

#Docker file quedo en la raiz del p
# definir el compose, para definirlo y correrlo.
# crear en el raiz un archivo.
