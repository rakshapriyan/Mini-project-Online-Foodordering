import java.util.Scanner;

public class Index {
	
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		System.out.println("Welcome to Zoroto.... Order your favourite items");
		System.out.println("New User? Enter 1 to Register into the website");
		System.out.println("Already a  User? Enter 2 to Login : ");
		System.out.println("Want to to changes with your profile? Enter 3");
		System.out.println("Want to delete your account? Enter 4");
		System.out.print("Your choice : ");
		String status = "start";
		while(!status.equals("exit")) {
			
			int LOGIN_OR_SIGNUP = sc.nextInt();
			if(LOGIN_OR_SIGNUP == 1) {
				registerUser(LOGIN_OR_SIGNUP);
			}
			else if(LOGIN_OR_SIGNUP == 2) {
				LoginUser();	
			}
			else if(LOGIN_OR_SIGNUP == 3) {
				UpdateUser.execute();
			}
			else if(LOGIN_OR_SIGNUP == 4) {
				DeleteUser.execute();
			}
			System.out.println("Type exit to end or type something else to continue :");
			status = sc.nextLine();
		}
		
		
	}
	
	static boolean registerUser(int LOGIN_OR_SIGNUP) {
		if(LOGIN_OR_SIGNUP == 1) {
			System.out.println("Enter your Full name : ");
			String name = sc.next();
			System.out.println("Enter your phone Number : ");
			String phone = sc.next();
			System.out.println("Enter your Password : ");
			String pwd = sc.next();
			System.out.println("Re-Enter your password : ");
			String repwd = sc.next();
			if(pwd.equals(repwd)) {
				Customer c = new Customer(name, phone, pwd);
				Register.signUpCustomer(c);
				System.out.println("User Registered");
				LoginUser();
				return true;
				
			}
			else {
				System.out.println("Password Doesn't match Try again");
				registerUser(LOGIN_OR_SIGNUP);
			}
		}
		registerUser(LOGIN_OR_SIGNUP);
		return true;
		
	}
	
	static void LoginUser() {
		System.out.println("Enter your phone Number : ");
		String phone = sc.next();
		System.out.println("Enter your Password : ");
		String pwd = sc.next();
		
		if(Login.login(phone, pwd) != -1) {
			System.out.println("Signed-In Succesfully");
			OrderFood.Order();
		}
		else {
			System.out.println("Retry again");
			LoginUser();
		}
		
		
	}

}
