package roue.src;


public class Action{
		private String nom;
		private float distance ;
		Action(String act, float dist)
		{
		setNom(act) ;
		setDistance(dist) ;
		}
		/**
		 * @return the action
		 */
		public String getNom() {
			return nom;
		}
		/**
		 * @param action the action to set
		 */
		public void setNom(String action) {
			this.nom = action;
		}
		/**
		 * @return the distance
		 */
		public float getDistance() {
			return distance;
		}
		/**
		 * @param distance the distance to set
		 */
		public void setDistance(float distance) {
			this.distance = distance;
		}
	}

