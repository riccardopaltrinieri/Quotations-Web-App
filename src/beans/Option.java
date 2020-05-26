package beans;

public class Option {

		private int id;
		private int optionCode;
		private String type;
		private String name;
		
		
		public Option(int id, int optionCode, String type, String name) {
			super();
			this.id = id;
			this.optionCode = optionCode;
			this.type = type;
			this.name = name;
		}
		
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getOptionCode() {
			return optionCode;
		}
		public void setOptionCode(int optionCode) {
			this.optionCode = optionCode;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		

}
