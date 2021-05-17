import ui.Window;

/**
 * Main class of the program
 * 
 * @author Yann Trividic
 * @version 1.0
 */

public class Main {

	public static final String DEFAULT_FOLDER = "train" ;
	
	public static void main(String[] args) throws Exception {
		nu.pattern.OpenCV.loadLocally(); // loads opencv for this run

		// Apply a look'n feel
		// UIManager.setLookAndFeel( new NimbusLookAndFeel() );
		Window myWindow = null ;
		
		switch(args.length) {
		case 0:
			System.out.println("Default folder: "+DEFAULT_FOLDER) ;
			myWindow = new Window(DEFAULT_FOLDER) ;
			break;
		case 1:
			if(args[0].equals("train") || args[0].equals("test") || args[0].equals("validation")) {
				System.out.println("Folder: "+args[0]) ;
				myWindow = new Window(args[0]);
			} else {
				System.out.println("Your argument: "+args[0]);
				System.out.println("Invalid command line arguments. Only \"train\", \"test\", and \"validation\" are available.") ;
			}
			break;
		default:
			System.out.println("You can enter at most one argument.") ;
		}

		if(myWindow != null) myWindow.setVisible(true);
	}
}