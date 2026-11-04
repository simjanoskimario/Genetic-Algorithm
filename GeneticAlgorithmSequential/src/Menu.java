import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    int gCount;
    int bCount;
    public Menu(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Evolution - Menu");
        this.setSize(200,150);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));

        JLabel lBrian = new JLabel("Number of Agents/Brians");
        JLabel lGens = new JLabel("Number of Generations");
        JTextField fBrian = new JTextField(6);
        JTextField fGens = new JTextField(6);
        JButton start = new JButton("Start Simulation");


        pan.add(lBrian);
        pan.add(fBrian);
        pan.add(lGens);
        pan.add(fGens);
        pan.add(start);
        this.add(pan);

        start.addActionListener(e -> {
            if(fBrian.getText() != null && fBrian.getText() != null){
                bCount = Integer.parseInt(fBrian.getText());
                gCount = Integer.parseInt(fGens.getText());
            }
            Environment env = new Environment(bCount,gCount);
        });
        this.setVisible(true);
    }
}
