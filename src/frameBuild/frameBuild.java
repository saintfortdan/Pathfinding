package frameBuild;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class frameBuild extends JFrame{
	
	private int n;
	
	//Constructor
	//Opening Frame asking for the the information for the plane size
	public frameBuild(){
		this.add(this.createPanel());
		this.setSize(300, 170);
		this.setLayout(null);
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//
	private JPanel createPanel(){
		//JLabel label = new JLabel();
		JPanel container = new JPanel();
		JLabel instructions = new JLabel("Please Enter A Number: 3 >= n <= 15");
		JLabel warning = new JLabel();
		JTextField planeSize = new JTextField();
		JButton enter = new JButton("Submit");

		planeSize.setBorder(BorderFactory.createLineBorder(new Color(0, 0 , 0), 2));
		planeSize.setBounds(45, 50, 200, 25);
		enter.setBounds(95, 82, 100, 25);
		instructions.setBounds(40, 24, 225, 15);
		warning.setBounds(95, 113, 200, 15);
		warning.setVisible(false);
		
		
		enter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					setBoard();
				}catch(Exception error) { System.err.println("Something is wrong!- \t" + error.getLocalizedMessage()); }
			}
		});
		
		planeSize.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					warning.setVisible(false);
					int temp = Integer.parseInt(planeSize.getText());
					if(temp >= 3 && temp <= 15) {
						n = temp;
						enter.setEnabled(true);
						if(e.getKeyCode() == 10) { setBoard(); }
					}
					else{
						warning.setText("Value is < 3 or > 15");
						warning.setVisible(true);
					}
				}
				catch(Exception error) {
					System.err.println("Something is wrong!- \t" + error.getLocalizedMessage());
					warning.setText("Not A Valid Input!");
					warning.setVisible(true);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});

		container.setBorder(BorderFactory.createLineBorder(Color.red));
		container.setBounds(5, 2, 275, 130);
		container.add(instructions);
		container.add(planeSize);
		container.add(enter);
		container.add(warning);
		container.setLayout(null);
		return container;
	}	
	
	private void setBoard(){
		this.dispose();
		new planeBuild(this.n);
	}
}
