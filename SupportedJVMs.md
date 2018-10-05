# List of currently supported JVMs

* Sun Hotspot VM, versions 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 9, 10, 11
* OpenJDK 6, 7, 8, 9, 10, 11
* Android API level 8 to 26 (Serialization support starting from Honeycomb)
* Azul Zulu (tested on 1.8.0_45-b14)
* Azul Zing JDK 1.8 (tested on 1.8.0-zing_16.10.1.0-b2)
* IKVM (tested on 8.1.5717)
* BEA JRockit R27, R28
* GCJ version 3.4.4 (tested on Windows/Cygwin)
* Aonix PERC (no serialization support), tested on version 5.0.0667

* Version 1.x supports Java 1.3 to 1.7
* Version 2.x supports Java 1.5 to 10
* Version 3.x supports Java 1.8 to 11
 
## Google App Engine 

GAE is only really partially supported due to the constraints of the platform.

Only serializable objects can be instantiated. So the Serializing instantiator will behave almost correctly. There
is only one loophole: `readResolve` will be called if implemented by the created class. This isn't the case
on the other platforms.

The Standard instantiator will 
* call the constructor from the first non-serializable parent if the class is serializable
* the default constructor if the class is not
 
This is far from ideal but will kinda work in some cases. Calling a constructor defeats a bit the purpose of
Objenesis to say the least...

If someone has a better solution, we are listening. This is the result of the TCK. Remember that 'Y' means
the class was instantiated without exception. It doesn't mean that no constructor was called.

|Class type                                        |Objenesis serializer|Objenesis std|
|---------------------------------------------------|:-----------------:|:-----------:|
|Constructor throwing exception                     |N/A                |n            |
|Constructor throwing exception (serializable)      |Y                  |Y            |
|Constructor with arguments                         |N/A                |n            |
|Constructor with arguments (serializable)          |Y                  |Y            |
|Constructor with mandatory arguments               |N/A                |n            |
|Constructor with mandatory arguments (serializable)|Y                  |Y            |
|Default package constructor                        |N/A                |Y            |
|Default package constructor (serializable)         |Y                  |Y            |
|Default private constructor                        |N/A                |Y            |
|Default private constructor (serializable)         |Y                  |Y            |
|Default protected constructor                      |N/A                |Y            |
|Default protected constructor (serializable)       |Y                  |Y            |
|Default public constructor                         |N/A                |Y            |
|Default public constructor (serializable)          |Y                  |Y            |
|No constructor                                     |N/A                |Y            |
|No constructor (serializable)                      |Y                  |Y            |
|Serializable replacing with another class          |Y                  |Y            |
|Serializable resolving to another class            |n                  |n            |
|Serializable with ancestor throwing exception      |N/A                |n            |
