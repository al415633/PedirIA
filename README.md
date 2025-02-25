<img src="./assets/logo.png" alt="PedirIA Logo" width="100" align="left">

# PedirIA

![Java Version](https://img.shields.io/badge/java-17-blue) ![React Version](https://img.shields.io/badge/react-18.2.0-blue) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-green) ![Firebase](https://img.shields.io/badge/Firebase-Active-yellow)

PedirIA es una plataforma diseñada para optimizar la gestión de stock en pequeños y medianos comercios mediante inteligencia artificial. Facilita la predicción de demanda, la automatización de reabastecimientos y la conexión con organizaciones para la redistribución de excedentes.

## Autores del Proyecto

| Miembro 1 - Ángela Ausina Sánchez       | Miembro 2 - Andrea Belen Cretu Toma    | Miembro 3 - Alejandro Díaz Rivero    | Miembro 4 - Alejandro Tendero Ferrandis  | Miembro 5 - Hugo Martí Fernández  | Miembro 6 - Óscar Renau Pallarés  |
|----------------------------------------|----------------------------------------|----------------------------------------|-------------------------------------------|-------------------------------------------|-------------------------------------------|
| <img src="https://avatars.githubusercontent.com/u/95291485?v=4" alt="Ángela Ausina Sánchez" width="120"/>| <img src="https://avatars.githubusercontent.com/u/95291876?v=4" alt="Andrea Belen Cretu Toma" width="120"/> | <img src="https://avatars.githubusercontent.com/u/97406896?v=4" alt=" Alejandro Díaz Rivero" width="120"/> | <img src="https://avatars.githubusercontent.com/u/114917263?v=4" alt="Alejandro Tendero Ferrandis" width="120"/> | <img src="https://avatars.githubusercontent.com/u/165664933?v=4" alt="Hugo Martí Fernández " width="120"/> | <img src="https://avatars.githubusercontent.com/u/95293214?v=4" alt="Óscar Renau Pallarés " width="120"/> | 

## Tecnologías Utilizadas

### Backend
- **Java (Quarkus):** Framework ligero y optimizado para la creación de microservicios.
- **PostgreSQL:** Base de datos relacional para almacenamiento seguro y consultas eficientes.
- **Firebase:** Gestión de autenticación y almacenamiento en la nube.

### Frontend
- **React:** Biblioteca para el desarrollo de interfaces dinámicas y responsivas.

### API y Servicios
- **API de Ubicación:** Para la geolocalización de comercios y optimización de rutas de entrega.

## Instalación y Configuración

### Requisitos previos
- Java 17+
- Node.js y npm
- PostgreSQL instalado y configurado

### Pasos de instalación
**Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu_usuario/pedirIA.git
   cd pedirIA
   ```

### Funcionalidades Exploradas
- **Gestión de stock y predicción de demanda con IA**:
- **Interfaz intuitiva para administración de productos y reportes**:
- **Integración con Firebase para autenticación segura**:
- **Geolocalización para optimizar reabastecimientos**:
- **Base de datos PostgreSQL para almacenamiento eficiente**:


## Contribuir al Proyecto

> [!TIP]
> Recomendamos usar Visual Studio Code para utilizar el código.

En este proyecto utilizamos una metodología basada en **Gitflow** para trabajar de forma colaborativa y organizada. A continuación te explicamos cómo contribuir correctamente.

### Buenas Prácticas para Commits
Seguimos el formato de **Conventional Commits** para que todos los mensajes de commit sean claros y consistentes. El formato a seguir es:

```bash
   <type>(<scope>): <description>
```

- **type**: Tipo de cambio, puede ser uno de los siguientes:
    - `test`: cambios relacionados con las pruebas.
    - `feat`: cambios que introducen una nueva funcionalidad al proyecto.
    - `refactor`: cambios que introducen modificaciones para optimizar el diseño de funcionalidades previamente implementadas.
    - `fix`:  cambios para arreglar bugs.
    - `chore`: cambios relacionados con la configuración del proyecto. Por ejemplo, añadir/modificar dependencias.

- **scope**: indica donde se producen los cambios . En nuestro caso lo vamos a usar para indicar a que iteración e historia pertenece el commit. Lo indicaremos de la siguiente forma: itnnhxx, donde nn será en número de iteración y xx el número de historia.

- **description**: breve descripción de los cambios introducidos en el commit, en inglés. Intentad utilizar descripciones cortas y concisas, pero que aporten información .Si no sois capaces de describir los cambios que incluye el commit de forma concisa y breve es posible que necesitéis separar los cambios en varios commits.

### Ramas de Trabajo
- **Rama principal (`main`)**: Contiene solo versiones estables del proyecto. No se trabaja directamente en esta rama.
- **Ramas de iteración (`it-nn`)**: Cada iteración del desarrollo tiene su propia rama. Por ejemplo, la tercera iteración se trabaja en la rama `it-03`. Aquí se fusionan todas las historias de esa iteración.
- **Ramas de historia (`h-xx`)**: Cada historia o funcionalidad tiene su propia rama. Por ejemplo, la historia 5 en la tercera iteración se trabaja en la rama `h-05`. Los cambios de cada historia se implementan en su propia rama.

### Flujo de Trabajo
1. **Crear una rama de iteración**: Al comenzar a trabajar en una iteración, crea una nueva rama desde la rama de main:
   ```bash
   git checkout -b it-01

2. **Crear una rama de historia**: Al comenzar a trabajar en una historia, crea una nueva rama desde la rama de la iteración correspondiente:
   ```bash
   git checkout -b h-05

3. **Hacer un pull request**: Al acabar de programar la funcionalidad deseada, abre un PR para que todos los miembros del equipo puedan revisar tus cambios
   `IT03-H05: Implement user login functionality`

4. **Revisión y fusión**: El equipo revisará los cambios en la PR. Si todo está en orden, se aprobará la PR y los cambios se fusionarán con la rama de iteración (`it-nn`):
   ```bash
   git checkout it-03
   git merge h-05
   ```

5. **Fusión a main**: Una vez que todas las historias de una iteración estén completas y revisadas, se abrirá una PR para fusionar la rama de iteración con la rama principal (main). Esta fusión solo debe ocurrir cuando se haya comprobado que la iteración es estable.
   ```bash
   git checkout main
   git merge it-03  
   ```

## Estructura del Proyecto

```bash
├── lib
│   ├── model    
│        └── enum  # Carpeta con las enumeraciones utilizadas
│   ├── view     # Carpeta con las vistas/interfaces creadas
│   ├── viewModel     # Carpeta con los controladores
│        └── adapters  # Carpeta con los adaptadores, los que acceden a la base de datos
│   └── main.dart   # Página principal en la que mostramos el login
├── test            # Carpeta referente a los tests
│   ├── acceptance_tests    # Carpeta con los tests de aceptación
│   └── integration_tests   # Carpeta con los tests de integración
└── README.md
```


## Agradecimientos
Darle las gracias a todos los profesores de las asignaturas que han hecho posible este último empujoncito de cara a nuestra vida laboral. 
