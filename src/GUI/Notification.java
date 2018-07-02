package GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import main.Consts;

public class Notification {
	
	public static void notifi(String message) {
		String header = "Notification:";
		JFrame frame = new JFrame();

		frame.setSize(Consts.notificationPopupWidth, Consts.notificationPopupHeight);
		frame.setLayout(new GridBagLayout());
		
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0f;
		constraints.weighty = 1.0f;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		
		JLabel headingLabel = new JLabel(header);
		// headingLabel .setIcon(headingIcon); // --- use image icon you want to
		headingLabel.setOpaque(false);
		frame.add(headingLabel, constraints);
		
		constraints.gridx++;
		constraints.weightx = 0f;
		constraints.weighty = 0f;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTH;
		
		JButton closeNotification = new JButton();
		closeNotification.setMargin(new Insets(1, 4, 1, 4));
		closeNotification.setFocusable(false);
		frame.add(closeNotification, constraints);
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.weightx = 1.0f;
		constraints.weighty = 1.0f;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		JLabel messageLabel = new JLabel("<HtMl>" + message);
		
		frame.setLocation(560+Consts.WIDTH-frame.getWidth(),300);
		frame.add(messageLabel, constraints);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setVisible(true);
		
		closeNotification.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
		});

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000); 
					frame.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

}
