---------------------- JASEIMOV ----------------------------------------
Autor: Aday Talavera Hierro <aday.talavera@gmail.com>

Aplicación realizada como parte del proyecto fin de carrera: 
Diseño hardware/software para el control y monitorización de un vehículo
eléctrico inteligente a escala

Tutores del proyecto:
Dr. D. Javier J. Sánchez Medina
Dr. D. Enrique Rubio Royo

Para la obtención del título de Ingeniero Informático en la Facultad de
Informática de la Universidad de Las Palmas de Gran Canaria

---------------------- Contenidos --------------------------------------

Proyectos de Netbeans 6.8 compilados utilizando el openjdk 6:

- ./client: aplicación cliente
- ./server: aplicación servidor
- ./comm-lib: librería de comunicación entre cliente y servidor
- ./testAseimov: algunos test de ASEIMOV
- ./libraries: librerías en formato jar y sus javadoc
                     
Scripts de inicio:

 - client.sh: ejecuta el cliente de JASEIMOV
 - server.sh: ejecuta el servidor de JASEIMOV configurándolo con el
              archivo config del mismo directorio
 - server-test.sh: ejecuta el servidor de JASEIMOV en modo test
 - javadoc.sh: compilar la documentación javadoc de los proyectos en la
               carpeta ./JAVADOC
               
----------------- Dependencias del servidor ----------------------------

El servidor requiere de la instalación de las librerías siguientes en el
sistema:

- Librería phidgets versión 2.1.5 con soporte JNI
- Librería v4l4j versión 0.8.7

Las dos librerías se encuentran en la carpeta ./otros

----------------- Problemas conocidos del servidor ---------------------

Es necesario que en el archivo /etc/hosts de la distribución tenga bien
puesta la IP del sistema.

Ejemplo de archivo /etc/hosts :

127.0.0.1	localhost
192.168.0.1	nombre-maquina

La IP a la izquierda de nombre-maquina debe estar puesta correctamente o
pueden surgir errores a la hora de utilizar el servidor


