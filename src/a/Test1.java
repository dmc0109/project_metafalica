package a;

public class Test1 {
	public static void main(String[] args) {
		class A{
			private String s;
			public A(String s) {
				this.s = s;
			}
			public void print() {
				System.out.println(s);
			}
		}
		new A("hello").print();
		new B("Helob").print();
	}
	static class B{
		private String s;
		public B(String s) {
			this.s = s;
		}
		public void print() {
			System.out.println(s);
		}
	}
}
