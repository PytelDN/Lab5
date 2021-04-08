import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class FractalExplorer {

    private JImageDisplay jImageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double range;
    private int displaySize;
    JComboBox<FractalGenerator> jComboBox;

    public FractalExplorer(int displaySize) {
        this.displaySize = displaySize;
        this.range = new Rectangle2D.Double();
        this.fractalGenerator = new Mandelbrot();
        fractalGenerator.getInitialRange(range);
    }

    public void createAndShowGUI(){
        JFrame jFrame = new JFrame("Fractal Explorer");
        jFrame.setLayout(new BorderLayout());
        JPanel jPanelComboBox = new JPanel();
        JPanel jPanelButtons = new JPanel();
        jImageDisplay = new JImageDisplay(displaySize,displaySize);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.addMouseListener(new MouseHandler());

        // Инициализация объектов jPanelComboBox
        jComboBox = new JComboBox<>();
        jComboBox.addItem(new Mandelbrot());
        jComboBox.addItem(new Tricorn());
        jComboBox.addItem(new BurningShip());
        jComboBox.addActionListener(new FractalHandler());
        JLabel jLabel = new JLabel("Fractal: ");
        jPanelComboBox.add(jLabel,BorderLayout.WEST);
        jPanelComboBox.add(jComboBox,BorderLayout.EAST);

        // Инициализация объектов jPanelButtons
        JButton jButtonRS = new JButton("Reset Display");
        jButtonRS.addActionListener(new ResetHandler());
        JButton jButtonSS = new JButton("Save Screen");
        jButtonSS.addActionListener(new SaveHandler());
        jPanelButtons.add(jButtonSS,BorderLayout.EAST);
        jPanelButtons.add(jButtonRS,BorderLayout.WEST);

        // Добавление в jFrame
        jFrame.add(jImageDisplay,BorderLayout.CENTER);
        jFrame.add(jPanelButtons,BorderLayout.SOUTH);
        jFrame.add(jPanelComboBox,BorderLayout.NORTH);

        drawFractal();
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setResizable(false);
    }

    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(800);
        fractalExplorer.createAndShowGUI();
        fractalExplorer.drawFractal();

    }

    private void drawFractal(){
        for (int x = 0; x < jImageDisplay.getBufferedImage().getWidth(); x++){
            double xCoord = fractalGenerator.getCoord(range.x,  range.x  +  range.width, displaySize, x);
            for (int y = 0; y < jImageDisplay.getBufferedImage().getHeight(); y++){
                double yCoord = fractalGenerator.getCoord(range.y,  range.y  +  range.height, displaySize, y);
                if (fractalGenerator.numIterations(xCoord,yCoord)==-1) {
                    jImageDisplay.drawPixel(x,y,0);
                } else {
                    float hue = 0.7f + (float) fractalGenerator.numIterations(xCoord,yCoord) / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    jImageDisplay.drawPixel(x,y,rgbColor);
                }
            }
        }
        jImageDisplay.repaint();
    }

    private class ResetHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            range = new Rectangle2D.Double();
            fractalGenerator.getInitialRange(range);
            drawFractal();
        }
    }

    private class SaveHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser showDialog = new JFileChooser();
            FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG", "png");
            showDialog.setFileFilter(pngFilter);
            showDialog.setAcceptAllFileFilterUsed(false);
            if (showDialog.showSaveDialog(jImageDisplay) == JFileChooser.APPROVE_OPTION){
                java.io.File file = showDialog.getSelectedFile();
                String fileName = file.toString();
                String filePath = file.getPath();
                if(!filePath.toLowerCase().endsWith(".png"))
                {
                    file = new java.io.File(filePath + ".png");
                }
                try {
                    BufferedImage bufferedImage = jImageDisplay.getBufferedImage();
                    javax.imageio.ImageIO.write(bufferedImage, "png", file);
                }
                catch (Exception exception){
                    JOptionPane.showMessageDialog(jImageDisplay, "Произошла ошибка при сохранении файла " + fileName
                            + " Лог ошибки:" + exception.getMessage());

                }
            }
        }
    }

    private class FractalHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fractalGenerator = (FractalGenerator) jComboBox.getSelectedItem();
            range = new Rectangle2D.Double();
            fractalGenerator.getInitialRange(range);
            drawFractal();
        }
    }


    private class MouseHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width,
                    displaySize, e.getX());
            double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height,
                    displaySize, e.getY());

            fractalGenerator.recenterAndZoomRange(range,xCoord, yCoord, 0.5);
            drawFractal();
        }
    }
}
