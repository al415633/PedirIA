<template>
  <header class="navbar_container">
    <div id="app" class="navbar-box">
      <img src="images/logo_cropped.png" alt="PEDIRIA" class="small_logo">
      <a href="#" class="btn">Mis datos</a>
      <a href="#" class="btn">Stock</a>
      <a href="#" class="btn">Modificar datos</a>

      <div class="dropdown">
        <a href="#" class="btn">Generar informe</a>
        <div class="dropdown-content">
          <a href="predecir_carnes.html">Predecir Carnes</a>
          <a href="predecir_pescados.html">Predecir Pescados</a>
          <a href="predecir_hortofruticolas.html">Predecir Hortofrutícolas</a>
        </div>
      </div>

      <a href="#" class="btn">{{ comercioActivo }}</a>
    </div>
  </header>
</template>

<script>
import "js/comercio.js";
export default {
  data() {
    return {
      comercioActivo: "sin sesion"
    };
  },
  mounted() {
    this.getActiveComercio();
  },
  methods: {
    async getActiveComercio() {
      try {
        const response = await axios.get("/comercio/obtener");
        this.comercioActivo = response.data;
      } catch (error) {
        console.error("Error al obtener los datos:", error);
      }
    }
  }
};
</script>

<style scoped>
/* Estilos del navbar y dropdown */
.navbar_container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.dropdown {
  position: relative;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  min-width: 180px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  top: 100%;
  left: 0;
  border-radius: 4px;
  overflow: hidden;
}

.dropdown-content a {
  color: black;
  padding: 10px;
  display: block;
  text-decoration: none;
  background: white;
}

.dropdown-content a:hover {
  background-color: #f1f1f1;
}

.dropdown:hover .dropdown-content {
  display: block;
}
</style>
