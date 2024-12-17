package click_set_secure;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class PROJ1_phase2 {
    private static final String ACCESS_PIN = "S@160803"; 
    private static int pinAttempts = 0; 
    private static final int MAX_PIN_ATTEMPTS = 3;

    public class PasswordInterface {
        private JFrame mainFrame;
        private JPanel buttonPanel;
        private ArrayList<ImageIcon> passwordIcons;
        private ArrayList<String> storedSequence;
        private ArrayList<String> selectedImages;
        private boolean isPasswordBeingSet;
        private JLabel strengthIndicator;
        private Timer countdownTimer;
        private int countdownSeconds;

        public PasswordInterface() {
            if (!requestPin()) {
                return; 
            }

            setupPasswordInterface(); 
        }

        private void setupPasswordInterface() {
            storedSequence = new ArrayList<>(Arrays.asList("img1", "img2", "img3"));
            selectedImages = new ArrayList<>();
            isPasswordBeingSet = false;

            passwordIcons = new ArrayList<>();
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/brown.png")));
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/green.png")));
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/yellow.jpeg")));
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/orange.png")));
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/blue.png")));
            passwordIcons.add(new ImageIcon(getClass().getResource("/images/grey.png")));
            Collections.shuffle(passwordIcons); 

            mainFrame = new JFrame("Password Management System");
            mainFrame.setSize(500, 500);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(2, 3));

            strengthIndicator = new JLabel("Password Strength: ");
            mainFrame.add(strengthIndicator, BorderLayout.NORTH);

            for (ImageIcon icon : passwordIcons) {
                JButton button = new JButton(icon);
                button.addActionListener(new ImageSelectionHandler(icon.getDescription()));
                buttonPanel.add(button);
            }

            JButton setNewPasswordButton = new JButton("Set New Password");
            setNewPasswordButton.addActionListener(e -> {
                isPasswordBeingSet = true;
                selectedImages.clear();
                JOptionPane.showMessageDialog(mainFrame, "Select Your New Password Images");
                startTimer(); 
            });

            mainFrame.add(buttonPanel, BorderLayout.CENTER);
            mainFrame.add(setNewPasswordButton, BorderLayout.SOUTH);
            mainFrame.setVisible(true);
            startTimer(); 
        }

        public boolean requestPin() {
            while (pinAttempts < MAX_PIN_ATTEMPTS) {
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(null, passwordField, "Enter 4-digit PIN:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String pinInput = new String(passwordField.getPassword());

                    if (ACCESS_PIN.equals(pinInput)) {
                        return true;
                    } else {
                        pinAttempts++;
                        JOptionPane.showMessageDialog(null, "Incorrect PIN. Attempt " + pinAttempts + "/" + MAX_PIN_ATTEMPTS + ".");
                    }
                } else {
                    return false; // User canceled the input
                }
            }
            JOptionPane.showMessageDialog(null, "Max PIN attempts reached. Exiting.");
            System.exit(0); 
            return false;
        }

        public class ImageSelectionHandler implements ActionListener {
            public String imageName;

            public ImageSelectionHandler(String imageName) {
                this.imageName = imageName;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedImages.add(imageName);
                updatePasswordStrength();

                if (isPasswordBeingSet) {
                    if (selectedImages.size() == 3) {
                        storedSequence = new ArrayList<>(selectedImages);
                        JOptionPane.showMessageDialog(mainFrame, "New password set!");
                        selectedImages.clear();
                        isPasswordBeingSet = false;
                        stopTimer(); 
                    }
                } else {
                    if (selectedImages.size() == storedSequence.size()) {
                        if (selectedImages.equals(storedSequence)) {
                            JOptionPane.showMessageDialog(mainFrame, "Access Granted");
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Access Denied");
                        }
                        selectedImages.clear();
                    }
                }
            }
        }

        private void updatePasswordStrength() {
            int size = selectedImages.size();
            if (size == 1) {
                strengthIndicator.setText("Password Strength: Weak");
            } else if (size == 2) {
                strengthIndicator.setText("Password Strength: Medium");
            } else if (size == 3) {
                strengthIndicator.setText("Password Strength: Strong");
            } else {
                strengthIndicator.setText("Password Strength: ");
            }
        }

        private void startTimer() {
            countdownSeconds = 10; 
            countdownTimer = new Timer(1000, e -> {
                countdownSeconds--;
                if (countdownSeconds <= 0) {
                    JOptionPane.showMessageDialog(mainFrame, "Time's up! Returning to PIN entry.");
                    stopTimer();
                    mainFrame.dispose(); 
                    new PROJ1_phase2().new PasswordInterface(); 
                }
                strengthIndicator.setText(countdownSeconds + " seconds remaining");
            });
            countdownTimer.start(); 
        }

        private void stopTimer() {
            if (countdownTimer != null) {
                countdownTimer.stop();
                countdownTimer = null; 
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PROJ1_phase2().new PasswordInterface());
    }
}
