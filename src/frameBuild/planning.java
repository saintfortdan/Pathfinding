package frameBuild;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class planning {
		
	public class path{
	
		public int [][] someArray;
		path(){
			someArray = new int[4][4];
			//int [] i = new int[5];
			//Arrays.f(someArray, 0);
			for(int i = 0; i<4;i++)
			{
				for(int ii = 0; ii < someArray.length; ii++)
				{
					someArray[i][ii] = -1;
				}
			}
			someArray.toString();
		}
	}
	
		public static void main(String [] args){
			JFrame frame = new JFrame();
			Container c = new Container();
			JPanel box_01 = new JPanel();
			//box_01.setBounds(0, 0, 0, 0);
			box_01.setPreferredSize(new Dimension(50,150));
			box_01.setBackground(Color.WHITE);
			box_01.setName("01");
			box_01.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					//box_01.setForeground(Color.BLUE);
					
					if(Color.RED == box_01.getBackground())
					{
						box_01.setBackground(Color.WHITE);
					}else if(Color.WHITE == box_01.getBackground())
					{
						box_01.setBackground(Color.RED);
					}
					System.out.println(box_01.getName().split("", -1)[0]);
					//for(String s : box_01.getName().split("", -1))
						//System.out.println(s);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					//box_01.setSelectionColor(Color.BLUE);
				}
			});
			frame.setLayout(new BorderLayout());
			frame.add(box_01, BorderLayout.NORTH);
			System.out.println(frame.getComponent(0));
			frame.pack();
			frame.setSize(new Dimension(500,500));
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			planning temp = new planning();
			planning.path test = temp.new path();
			
			
	
		}
}
