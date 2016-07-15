package com.example.inter;

//Esta de el formato de cada entrada de la lista

public class Lista_entrada {

	

		private String textoSalida; 
		private String textoLlegada;
		private String textoDias;
		private String textoTarifa; 
		

		public Lista_entrada(String textoSalida, String textoLlegada,
				String textoDias, String textoTarifa) {
			super();
			this.textoSalida = textoSalida;
			this.textoLlegada = textoLlegada;
			this.textoDias = textoDias;
			this.textoTarifa = textoTarifa;
		}


		public String getTextoSalida() {
			return textoSalida;
		}


		public void setTextoSalida(String textoSalida) {
			this.textoSalida = textoSalida;
		}


		public String getTextoLlegada() {
			return textoLlegada;
		}


		public void setTextoLlegada(String textoLlegada) {
			this.textoLlegada = textoLlegada;
		}


		public String getTextoDias() {
			return textoDias;
		}


		public void setTextoDias(String textoDias) {
			this.textoDias = textoDias;
		}


		public String getTextoTarifa() {
			return textoTarifa;
		}


		public void setTextoTarifa(String textoTarifa) {
			this.textoTarifa = textoTarifa;
		}

		

		
		
	}

