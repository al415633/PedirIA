# ContactListJPA APP

Ejemplo de cómo utilizar [Quarkus](http://quarkus.io) para desarrollar una aplicación REST con el estándar JAX-RS.

La persistencia se hace con JPA.

Se utiliza PostgreSQL como gestor de bases de datos.
Debes crear un usuario llamado «hibernate» y dos bases de datos,
una de ellas llamada «production» y la otra «develop».

El front-end está desarrollado con [Vue 3](http://v3.vuejs.org).

Para las pruebas con Cucumber y Webdriver, necesitas descargarte el
driver adecuado para tu navegador, que encontrarás en
[esta página](https://www.selenium.dev/documentation/webdriver/getting_started/install_drivers/)