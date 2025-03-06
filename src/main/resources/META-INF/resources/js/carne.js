const RETRIEVE_ALL_CARNES = "/carnes";
const CARNE_ADD = "/carnes";
const DELETE = "/carnes/";
const RETRIEVE_ONE_CARNE = "/carnes/";
const UPDATE = "/carnes/update";

const STOCK_RETRIEVE_ONE = "/carnes/stock/retrieve/";
const STOCK_RETRIEVE = "/carnes/stock/producto/";
const STOCK_ADD = "/carnes/stock";
const STOCK_UPDATE = "/carnes/stock/";
const STOCK_DELETE = "/carnes/stock/";
const STOCK_PREDICT = "/carnes/stock/predict";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"];


Vue.createApp({
    data() {
        return {

            // Gestión de las carnes
            carnes: [],
            nombreCarne: "",
            unidadCarne: "",
            tipoConserva: "",
            carneSeleccionada: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación de carnes
            currentPage: 1,     // Página actual
            itemsPerPage: 8     // 8 tarjetas por página (2 filas x 4 columnas)
        };
    },

    // Permite hacer visible la enumeración constante    
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        },
        totalPages() {
            return Math.ceil(this.carnes.length / this.itemsPerPage);
        },
        displayedCarnes() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.carnes.slice(start, start + this.itemsPerPage);
        }
    },

    methods: {

        // Se encarga de obtener los datos de las imágenes
        onFileChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            this.imagenNombre = file.name;
            this.imagenTipo = file.type;

            const reader = new FileReader();
            reader.onload = (e) => {
                // Por ejemplo, podrías usar la representación en base64:
                this.imagenDatos = e.target.result;
            };
            reader.readAsDataURL(file);
        },

        // Métodos para paginación
        prevPage() {
            if (this.currentPage > 1) this.currentPage--;
        },
        nextPage() {
            if (this.currentPage < this.totalPages) this.currentPage++;
        },
        goToPage(page) {
            this.currentPage = page;
        },

        // Obtiene el listado de carnes completo
        async getAllCarnes() {
            try {
                const response = await axios.get(RETRIEVE_ALL_CARNES);
                console.log(response.data)
                this.carnes = response.data.map(carneArray => {
                    const carne = carneArray[0]; // El primer elemento es el objeto carne
                    const imagenNombre = carneArray[1]; // El segundo elemento es el nombre de la imagen
                    const imagenTipo = carneArray[2]; // El tercer elemento es el tipo de la imagen
                    const imagen = carneArray[3]; // El tercer elemento es la imagen

                    // Ahora aplanamos la información
                    return {
                        ...carne,
                        imagenNombre: imagenNombre,
                        imagenDatos: imagen, // Añadimos la imagen
                        imagenTipo: imagenTipo // Añadimos el tipo de la imagen
                    };
                });

                console.log(this.carnes);

                // Ordenar las carnes después
                this.carnes.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                console.log("Carnes cargadas:", this.carnes);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos de la carne a modificar
        async retrieveCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Recuperando Carne con ID:", carneId);
                const response = await axios.get(RETRIEVE_ONE_CARNE + carneId);
                this.carneSeleccionada = response.data;
            } catch (error) {
                console.error("Error al recuperar la Carne:", error);
            }
        },

        async createCarne() {
            try {
                // Construir el objeto Carne con un nombre consistente
                let newCarne = {
                    nombre: this.nombreCarne,
                    unidad: this.unidadCarne,
                    tipoConserva: this.tipoConserva,
                    // Campos de imagen que se asignarán en el DAO de forma transitoria:
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    // Si this.imagenDatos tiene el formato "data:image/jpeg;base64,...."
                    imagenDatos: this.imagenDatos.split(',')[1]
                };

                console.log("Carne a crear:", newCarne);

                // Llamada a la API utilizando await
                const response = await axios.post(CARNE_ADD, newCarne);
                console.log("Carne creada:", newCarne);

                // Agregar la nueva carne a la lista
                this.carnes.push(response.data);

                // Ordenar las carnes por nombre
                this.carnes.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Limpiar los campos del formulario
                this.nombreCarne = "";
                this.categoriaCarne = "";
                this.unidadCarne = "";
                this.tipoConserva = "";
                this.imagenNombre = "";
                this.imagenTipo = "";
                this.imagenDatos = null;

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Carne añadida con éxito!';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            } catch (error) {
                console.error("Error al crear Carne:", error);
                // Aquí podrías mostrar un toast de error o notificar al usuario
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Error al crear la carne. Inténtalo de nuevo.';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },

        // Se llama una vez se pulsa guardar en el modal de edición
        async updateCarne() {
            try {
                if (!this.carneSeleccionada.id) {
                    console.error("Error: ID de carne no definido.");
                    return;
                }
                console.log("Actualizando Carne:", this.carneSeleccionada);

                // Mostrar un mensaje de carga
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Actualizando carne...'; // Mensaje de carga
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

                // Realizar la solicitud de actualización
                const response = await axios.put(UPDATE, this.carneSeleccionada);

                // Verificar si la respuesta es exitosa
                if (response.status === 204) { // 204 No Content indica éxito, lógica en el resource
                    // Actualizar el listado de carnes localmente
                    const index = this.carnes.findIndex(carne => carne.id === this.carneSeleccionada.id);
                    if (index !== -1) {
                        this.carnes.splice(index, 1, {...this.carneSeleccionada}); // Reemplaza el elemento modificado
                    }

                    // Ordenar las carnes después de la actualización
                    this.carnes.sort((a, b) => a.nombre.localeCompare(b.nombre));

                    // Cerrar el modal manualmente
                    let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                    if (modal) {
                        modal.hide();
                    }

                    // Mostrar el toast de éxito
                    toastBody.textContent = 'Carne actualizada con éxito!'; // Mensaje de éxito
                    toastEl.classList.add('bg-success'); // Cambiar el color de fondo a verde
                    toast.show();
                } else {
                    // Lanzar una excepción si la respuesta no es 204
                    throw new Error('Error inesperado al actualizar la carne. Código de estado: ' + response.status);
                }
            } catch (error) {
                console.error("Error al actualizar Carne:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al actualizar la carne. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },

        // Llama al modal para confirmar el borrado
        deleteCarne(carneId) {
            // Mostrar el modal de confirmación
            const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
            modal.show();

            // Asignar el índice a un atributo del modal para usarlo en la confirmación
            const confirmButton = document.getElementById('confirmDeleteButton');
            confirmButton.onclick = () => {
                // Cerrar el modal aquí para evitar problemas
                modal.hide();
                this.confirmDelete(carneId);
            };
        },

        // Se encarga de comunicar con backend y eliminar la carne
        async confirmDelete(carneId) {

            try {
                console.log("Eliminado carne con ID")
                console.log("Eliminando Carne: ", this.carnes.find((element) => element.id === carneId));

                await axios.delete(DELETE + carneId);

                // Eliminar la carne del listado localmente
                this.carnes = this.carnes.filter(carne => carne.id !== carneId);

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Carne eliminada con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar Carne:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al eliminar la carne. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },


        // Para pedir una prediccion
        async predecirStock() {
            try {
                const response = await axios.get(STOCK_PREDICT);
                this.stockPrediction = response.data;

                console.log("predicción cargada:", response.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

    },


    mounted() {
        this.getAllCarnes();
    }
}).mount("#app");
