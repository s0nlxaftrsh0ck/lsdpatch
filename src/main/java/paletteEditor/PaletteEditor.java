package paletteEditor;/* Copyright (C) 2017 by Johan Kotlinski

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE. */

import java.awt.BorderLayout;

import java.awt.event.KeyEvent;
import java.io.File;

import utils.JFileChooserFactory;
import utils.JFileChooserFactory.FileOperation;
import utils.JFileChooserFactory.FileType;
import utils.RomUtilities;
import utils.StretchIcon;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class PaletteEditor
        extends JFrame
        implements java.awt.event.ItemListener, ChangeListener, java.awt.event.ActionListener {
    private static final long serialVersionUID = 5286120830758415869L;

    private byte romImage[] = null;
    private int paletteOffset = -1;
    private int nameOffset = -1;

    private final JPanel preview1a;
    private final JPanel preview1b;
    private final JPanel preview2a;
    private final JPanel preview2b;
    private final JPanel preview3a;
    private final JPanel preview3b;
    private final JPanel preview4a;
    private final JPanel preview4b;
    private final JPanel preview5a;
    private final JPanel preview5b;

    private final JLabel previewSongLabel;
    private final JLabel previewInstrLabel;

    private final JSpinner c1r1;
    private final JSpinner c1g1;
    private final JSpinner c1b1;
    private final JSpinner c1r2;
    private final JSpinner c1g2;
    private final JSpinner c1b2;
    private final JSpinner c2r1;
    private final JSpinner c2g1;
    private final JSpinner c2b1;
    private final JSpinner c2r2;
    private final JSpinner c2g2;
    private final JSpinner c2b2;
    private final JSpinner c3r1;
    private final JSpinner c3g1;
    private final JSpinner c3b1;
    private final JSpinner c3r2;
    private final JSpinner c3g2;
    private final JSpinner c3b2;
    private final JSpinner c4r1;
    private final JSpinner c4g1;
    private final JSpinner c4b1;
    private final JSpinner c4r2;
    private final JSpinner c4g2;
    private final JSpinner c4b2;
    private final JSpinner c5r1;
    private final JSpinner c5g1;
    private final JSpinner c5b1;
    private final JSpinner c5r2;
    private final JSpinner c5g2;
    private final JSpinner c5b2;

    private final JComboBox<String> paletteSelector;

    JMenuItem mntmPaste;

    private java.awt.image.BufferedImage songImage;
    private java.awt.image.BufferedImage instrImage;

    private int previousSelectedPalette = -1;

    private boolean updatingSpinners = false;
    private boolean populatingPaletteSelector = false;

    private javax.swing.JCheckBox desaturateButton;
    private boolean desaturate = false;

    public PaletteEditor() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(mnFile);

        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.addActionListener(this);
        mnFile.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.addActionListener(this);
        mnFile.add(saveMenuItem);

        JMenu mnEdit = new JMenu("Edit");
        mnEdit.setMnemonic(KeyEvent.VK_E);
        menuBar.add(mnEdit);

        JMenuItem mntmCopy = new JMenuItem("Copy Palette");
        mntmCopy.addActionListener(this);
        mntmCopy.setMnemonic(KeyEvent.VK_C);
        mnEdit.add(mntmCopy);

        mntmPaste = new JMenuItem("Paste Palette");
        mntmPaste.setMnemonic(KeyEvent.VK_P);
        mntmPaste.addActionListener(this);
        mntmPaste.setEnabled(false);
        mnEdit.add(mntmPaste);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setBounds(100, 100, 650, 656);
        setResizable(false);
        setTitle("Palette Editor");
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        paletteSelector = new JComboBox<>();
        paletteSelector.setBounds(10, 10, 140, 20);
        paletteSelector.setEditable(true);
        paletteSelector.addItemListener(this);
        paletteSelector.addActionListener(this);
        contentPane.add(paletteSelector);

        JPanel previewSong = new JPanel(new BorderLayout());
        previewSong.setBounds(314, 10, 160 * 2, 144 * 2);
        previewSongLabel = new JLabel();
        previewSong.add(previewSongLabel);
        contentPane.add(previewSong);

        JPanel previewInstr = new JPanel(new BorderLayout());
        previewInstr.setBounds(314, 10 + 144 * 2 + 10, 160 * 2, 144 * 2);
        previewInstrLabel = new JLabel();
        previewInstr.add(previewInstrLabel);
        contentPane.add(previewInstr);

        c1r1 = new JSpinner();
        c1r1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1r1.setBounds(10, 66, 36, 20);
        contentPane.add(c1r1);

        JLabel lblNormal = new JLabel("Normal");
        lblNormal.setBounds(10, 41, 46, 14);
        contentPane.add(lblNormal);

        c1g1 = new JSpinner();
        c1g1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1g1.setBounds(56, 66, 36, 20);
        contentPane.add(c1g1);

        c1b1 = new JSpinner();
        c1b1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1b1.setBounds(102, 66, 36, 20);
        contentPane.add(c1b1);

        preview1a = new JPanel();
        preview1a.setBounds(95, 41, 43, 14);
        preview1a.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview1a);

        preview1b = new JPanel();
        preview1b.setBounds(159, 41, 43, 14);
        preview1b.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview1b);

        c1b2 = new JSpinner();
        c1b2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1b2.setBounds(251, 66, 36, 20);
        contentPane.add(c1b2);

        c1g2 = new JSpinner();
        c1g2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1g2.setBounds(205, 66, 36, 20);
        contentPane.add(c1g2);

        c1r2 = new JSpinner();
        c1r2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c1r2.setBounds(159, 66, 36, 20);
        contentPane.add(c1r2);

        JLabel lblShaded = new JLabel("Shaded");
        lblShaded.setBounds(10, 97, 46, 14);
        contentPane.add(lblShaded);

        c2r1 = new JSpinner();
        c2r1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2r1.setBounds(10, 122, 36, 20);
        contentPane.add(c2r1);

        c2g1 = new JSpinner();
        c2g1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2g1.setBounds(56, 122, 36, 20);
        contentPane.add(c2g1);

        c2b1 = new JSpinner();
        c2b1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2b1.setBounds(102, 122, 36, 20);
        contentPane.add(c2b1);

        c2r2 = new JSpinner();
        c2r2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2r2.setBounds(159, 122, 36, 20);
        contentPane.add(c2r2);

        c2g2 = new JSpinner();
        c2g2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2g2.setBounds(205, 122, 36, 20);
        contentPane.add(c2g2);

        c2b2 = new JSpinner();
        c2b2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c2b2.setBounds(251, 122, 36, 20);
        contentPane.add(c2b2);

        preview2b = new JPanel();
        preview2b.setBounds(159, 97, 43, 14);
        preview2b.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview2b);

        preview2a = new JPanel();
        preview2a.setBounds(95, 97, 43, 14);
        preview2a.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview2a);

        JLabel lblAlternate = new JLabel("Alternate");
        lblAlternate.setBounds(10, 153, 82, 14);
        contentPane.add(lblAlternate);

        c3r1 = new JSpinner();
        c3r1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3r1.setBounds(10, 178, 36, 20);
        contentPane.add(c3r1);

        c3g1 = new JSpinner();
        c3g1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3g1.setBounds(56, 178, 36, 20);
        contentPane.add(c3g1);

        c3b1 = new JSpinner();
        c3b1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3b1.setBounds(102, 178, 36, 20);
        contentPane.add(c3b1);

        c3r2 = new JSpinner();
        c3r2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3r2.setBounds(159, 178, 36, 20);
        contentPane.add(c3r2);

        c3g2 = new JSpinner();
        c3g2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3g2.setBounds(205, 178, 36, 20);
        contentPane.add(c3g2);

        c3b2 = new JSpinner();
        c3b2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c3b2.setBounds(251, 178, 36, 20);
        contentPane.add(c3b2);

        preview3b = new JPanel();
        preview3b.setBounds(159, 153, 43, 14);
        preview3b.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview3b);

        preview3a = new JPanel();
        preview3a.setBounds(95, 153, 43, 14);
        preview3a.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview3a);

        JLabel lblCursor = new JLabel("Selection");
        lblCursor.setBounds(10, 209, 82, 14);
        contentPane.add(lblCursor);

        c4r1 = new JSpinner();
        c4r1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4r1.setBounds(10, 234, 36, 20);
        contentPane.add(c4r1);

        c4g1 = new JSpinner();
        c4g1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4g1.setBounds(56, 234, 36, 20);
        contentPane.add(c4g1);

        c4b1 = new JSpinner();
        c4b1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4b1.setBounds(102, 234, 36, 20);
        contentPane.add(c4b1);

        c4r2 = new JSpinner();
        c4r2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4r2.setBounds(159, 234, 36, 20);
        contentPane.add(c4r2);

        c4g2 = new JSpinner();
        c4g2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4g2.setBounds(205, 234, 36, 20);
        contentPane.add(c4g2);

        c4b2 = new JSpinner();
        c4b2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c4b2.setBounds(251, 234, 36, 20);
        contentPane.add(c4b2);

        preview4b = new JPanel();
        preview4b.setBounds(159, 209, 43, 14);
        preview4b.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview4b);

        preview4a = new JPanel();
        preview4a.setBounds(95, 209, 43, 14);
        preview4a.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview4a);

        JLabel scrollStartLabel = new JLabel("Scroll");
        scrollStartLabel.setBounds(10, 265, 65, 14);
        contentPane.add(scrollStartLabel);

        c5r1 = new JSpinner();
        c5r1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5r1.setBounds(10, 290, 36, 20);
        contentPane.add(c5r1);

        c5g1 = new JSpinner();
        c5g1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5g1.setBounds(56, 290, 36, 20);
        contentPane.add(c5g1);

        c5b1 = new JSpinner();
        c5b1.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5b1.setBounds(102, 290, 36, 20);
        contentPane.add(c5b1);

        c5r2 = new JSpinner();
        c5r2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5r2.setBounds(159, 290, 36, 20);
        contentPane.add(c5r2);

        c5g2 = new JSpinner();
        c5g2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5g2.setBounds(205, 290, 36, 20);
        contentPane.add(c5g2);

        c5b2 = new JSpinner();
        c5b2.setModel(new SpinnerNumberModel(0, 0, 31, 1));
        c5b2.setBounds(251, 290, 36, 20);
        contentPane.add(c5b2);

        desaturateButton = new javax.swing.JCheckBox("Desaturate preview");
        desaturateButton.setBounds(10, 330, 146, 24);
        desaturateButton.addItemListener(this);
        contentPane.add(desaturateButton);

        preview5b = new JPanel();
        preview5b.setBounds(159, 265, 43, 14);
        preview5b.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview5b);

        preview5a = new JPanel();
        preview5a.setBounds(95, 265, 43, 14);
        preview5a.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
        contentPane.add(preview5a);

        listenToSpinners();

        try {
            songImage = javax.imageio.ImageIO.read(getClass().getResource("/song.bmp"));
            instrImage = javax.imageio.ImageIO.read(getClass().getResource("/instr.bmp"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToSpinners() {
        c1r1.addChangeListener(this);
        c1g1.addChangeListener(this);
        c1b1.addChangeListener(this);
        c1r2.addChangeListener(this);
        c1g2.addChangeListener(this);
        c1b2.addChangeListener(this);
        c2r1.addChangeListener(this);
        c2g1.addChangeListener(this);
        c2b1.addChangeListener(this);
        c2r2.addChangeListener(this);
        c2g2.addChangeListener(this);
        c2b2.addChangeListener(this);
        c3r1.addChangeListener(this);
        c3g1.addChangeListener(this);
        c3b1.addChangeListener(this);
        c3r2.addChangeListener(this);
        c3g2.addChangeListener(this);
        c3b2.addChangeListener(this);
        c4r1.addChangeListener(this);
        c4g1.addChangeListener(this);
        c4b1.addChangeListener(this);
        c4r2.addChangeListener(this);
        c4g2.addChangeListener(this);
        c4b2.addChangeListener(this);
        c5r1.addChangeListener(this);
        c5g1.addChangeListener(this);
        c5b1.addChangeListener(this);
        c5r2.addChangeListener(this);
        c5g2.addChangeListener(this);
        c5b2.addChangeListener(this);
    }

    public void setRomImage(byte[] romImage) {
        this.romImage = romImage;
        paletteOffset = RomUtilities.findPaletteOffset(romImage);
        if (paletteOffset == -1) {
            System.err.println("Could not find palette offset!");
        }
        nameOffset = RomUtilities.findPaletteNameOffset(romImage);
        if (nameOffset == -1) {
            System.err.println("Could not find palette name offset!");
        }
        populatePaletteSelector();
    }

    private int selectedPalette() {
        int palette = paletteSelector.getSelectedIndex();
        assert palette >= 0;
        assert palette < RomUtilities.getNumberOfPalettes(romImage);
        return palette;
    }

    // Returns color scaled to 0-0xf8.
    private java.awt.Color color(int offset) {
        // gggrrrrr 0bbbbbgg
        int r = (romImage[offset] & 0x1f) << 3;
        int g = ((romImage[offset + 1] & 3) << 6) | ((romImage[offset] & 0xe0) >> 2);
        int b = (romImage[offset + 1] << 1) & 0xf8;
        return new java.awt.Color(r, g, b);
    }

    private void updateRom(int offset,
                           JSpinner sr1,
                           JSpinner sg1,
                           JSpinner sb1,
                           JSpinner sr2,
                           JSpinner sg2,
                           JSpinner sb2) {
        int r1 = (Integer) sr1.getValue();
        int g1 = (Integer) sg1.getValue();
        int b1 = (Integer) sb1.getValue();
        // gggrrrrr 0bbbbbgg
        romImage[offset] = (byte) (r1 | (g1 << 5));
        romImage[offset + 1] = (byte) ((g1 >> 3) | (b1 << 2));

        int r2 = (Integer) sr2.getValue();
        int g2 = (Integer) sg2.getValue();
        int b2 = (Integer) sb2.getValue();
        romImage[offset + 6] = (byte) (r2 | (g2 << 5));
        romImage[offset + 7] = (byte) ((g2 >> 3) | (b2 << 2));

        // Generating antialiasing colors.
        int rMid = (r1 + r2) / 2;
        int gMid = (g1 + g2) / 2;
        int bMid = (b1 + b2) / 2;
        romImage[offset + 2] = (byte) (rMid | (gMid << 5));
        romImage[offset + 3] = (byte) ((gMid >> 3) | (bMid << 2));
        romImage[offset + 4] = romImage[offset + 2];
        romImage[offset + 5] = romImage[offset + 3];
    }

    private int selectedPaletteOffset() {
        return paletteOffset + selectedPalette() * RomUtilities.PALETTE_SIZE;
    }

    private void updateRomFromSpinners() {
        updateRom(selectedPaletteOffset(), c1r1, c1g1, c1b1, c1r2, c1g2, c1b2);
        updateRom(selectedPaletteOffset() + 8, c2r1, c2g1, c2b1, c2r2, c2g2, c2b2);
        updateRom(selectedPaletteOffset() + 16, c3r1, c3g1, c3b1, c3r2, c3g2, c3b2);
        updateRom(selectedPaletteOffset() + 24, c4r1, c4g1, c4b1, c4r2, c4g2, c4b2);
        updateRom(selectedPaletteOffset() + 32, c5r1, c5g1, c5b1, c5r2, c5g2, c5b2);
    }

    private java.awt.Color firstColor(int colorSet) {
        assert colorSet >= 0;
        assert colorSet < RomUtilities.NUM_COLOR_SETS;
        int offset = selectedPaletteOffset() + colorSet * RomUtilities.COLOR_SET_SIZE;
        return color(offset);
    }

    private java.awt.Color secondColor(int colorSet) {
        assert colorSet >= 0;
        assert colorSet < RomUtilities.NUM_COLOR_SETS;
        int offset = selectedPaletteOffset() + colorSet * RomUtilities.COLOR_SET_SIZE + 3 * 2;
        return color(offset);
    }

    private java.awt.Color midColor(int colorSet) {
        assert colorSet >= 0;
        assert colorSet < RomUtilities.NUM_COLOR_SETS;
        int offset = selectedPaletteOffset() + colorSet * RomUtilities.COLOR_SET_SIZE + 2;
        return color(offset);
    }

    private String paletteName(int palette) {
        assert palette >= 0;
        assert palette < RomUtilities.getNumberOfPalettes(romImage);
        String s = "";
        s += (char) romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE];
        s += (char) romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 1];
        s += (char) romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 2];
        s += (char) romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 3];
        return s;
    }

    private void setPaletteName(int palette, String name) {
        if (name == null) {
            return;
        }
        if (name.length() >= RomUtilities.PALETTE_NAME_SIZE) {
            name = name.substring(0, RomUtilities.PALETTE_NAME_SIZE - 1);
        } else {
            StringBuilder nameBuilder = new StringBuilder(name);
            while (nameBuilder.length() < RomUtilities.PALETTE_NAME_SIZE - 1) {
                nameBuilder.append(" ");
            }
            name = nameBuilder.toString();
        }
        romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE] = (byte) name.charAt(0);
        romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 1] = (byte) name.charAt(1);
        romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 2] = (byte) name.charAt(2);
        romImage[nameOffset + palette * RomUtilities.PALETTE_NAME_SIZE + 3] = (byte) name.charAt(3);
    }

    private void populatePaletteSelector() {
        populatingPaletteSelector = true;
        paletteSelector.removeAllItems();
        // -2 to hide the GB palettes
        for (int i = 0; i < RomUtilities.getNumberOfPalettes(romImage); ++i) {
            paletteSelector.addItem(paletteName(i));
        }
        populatingPaletteSelector = false;
    }

    private int colorCorrect(java.awt.Color c) {
        int r = ((c.getRed() >> 3) * 255) / 0xf8;
        int g = ((c.getGreen() >> 3) * 255) / 0xf8;
        int b = ((c.getBlue() >> 3) * 255) / 0xf8;

        // Matrix conversion from Gambatte.
        return (((r * 13 + g * 2 + b) >> 1) << 16)
                | ((g * 3 + b) << 9)
                | ((r * 3 + g * 2 + b * 11) >> 1);
    }

    private java.awt.image.BufferedImage modifyUsingPalette(java.awt.image.BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
        java.awt.image.BufferedImage dstImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int rgb = srcImage.getRGB(x, y);
                java.awt.Color c;
                if (rgb == 0xff000000) {
                    c = firstColor(0);
                } else if (rgb == 0xff000008) {
                    c = midColor(0);
                } else if (rgb == 0xff000019) {
                    c = secondColor(0);
                } else if (rgb == 0xff000800) {
                    c = firstColor(1);
                } else if (rgb == 0xff000808) {
                    c = midColor(1);
                } else if (rgb == 0xff000819) {
                    c = secondColor(1);
                } else if (rgb == 0xff001000) {
                    c = firstColor(2);
                } else if (rgb == 0xff001008) {
                    c = midColor(2);
                } else if (rgb == 0xff001019) {
                    c = secondColor(2);
                } else if (rgb == 0xff001900) {
                    c = firstColor(3);
                } else if (rgb == 0xff001908) {
                    c = midColor(3);
                } else if (rgb == 0xff001919) {
                    c = secondColor(3);
                } else if (rgb == 0xff002100) {
                    c = firstColor(4);
                } else if (rgb == 0xff002108) {
                    c = midColor(4);
                } else if (rgb == 0xff002119) {
                    c = secondColor(4);
                } else {
                    System.err.println(String.format("%x", rgb));
                    c = new java.awt.Color(255, 0, 255);
                }
                dstImage.setRGB(x, y, colorCorrect(c));
            }
        }
	if (desaturate) {
		return new java.awt.image.ColorConvertOp(java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY), null).filter(dstImage, dstImage);
	}
        return dstImage;
    }

    private void updateSongAndInstrScreens() {
        previewSongLabel.setIcon(new StretchIcon(modifyUsingPalette(songImage)));
        previewInstrLabel.setIcon(new StretchIcon(modifyUsingPalette(instrImage)));
    }

    private void updatePreviewPanes() {
        preview1a.setBackground(new java.awt.Color(colorCorrect(firstColor(0))));
        preview1b.setBackground(new java.awt.Color(colorCorrect(secondColor(0))));
        preview2a.setBackground(new java.awt.Color(colorCorrect(firstColor(1))));
        preview2b.setBackground(new java.awt.Color(colorCorrect(secondColor(1))));
        preview3a.setBackground(new java.awt.Color(colorCorrect(firstColor(2))));
        preview3b.setBackground(new java.awt.Color(colorCorrect(secondColor(2))));
        preview4a.setBackground(new java.awt.Color(colorCorrect(firstColor(3))));
        preview4b.setBackground(new java.awt.Color(colorCorrect(secondColor(3))));
        preview5a.setBackground(new java.awt.Color(colorCorrect(firstColor(4))));
        preview5b.setBackground(new java.awt.Color(colorCorrect(secondColor(4))));

        updateSongAndInstrScreens();
    }

    private void updateSpinners() {
        updatingSpinners = true;
        c1r1.setValue(firstColor(0).getRed() >> 3);
        c1g1.setValue(firstColor(0).getGreen() >> 3);
        c1b1.setValue(firstColor(0).getBlue() >> 3);
        c1r2.setValue(secondColor(0).getRed() >> 3);
        c1g2.setValue(secondColor(0).getGreen() >> 3);
        c1b2.setValue(secondColor(0).getBlue() >> 3);
        c2r1.setValue(firstColor(1).getRed() >> 3);
        c2g1.setValue(firstColor(1).getGreen() >> 3);
        c2b1.setValue(firstColor(1).getBlue() >> 3);
        c2r2.setValue(secondColor(1).getRed() >> 3);
        c2g2.setValue(secondColor(1).getGreen() >> 3);
        c2b2.setValue(secondColor(1).getBlue() >> 3);
        c3r1.setValue(firstColor(2).getRed() >> 3);
        c3g1.setValue(firstColor(2).getGreen() >> 3);
        c3b1.setValue(firstColor(2).getBlue() >> 3);
        c3r2.setValue(secondColor(2).getRed() >> 3);
        c3g2.setValue(secondColor(2).getGreen() >> 3);
        c3b2.setValue(secondColor(2).getBlue() >> 3);
        c4r1.setValue(firstColor(3).getRed() >> 3);
        c4g1.setValue(firstColor(3).getGreen() >> 3);
        c4b1.setValue(firstColor(3).getBlue() >> 3);
        c4r2.setValue(secondColor(3).getRed() >> 3);
        c4g2.setValue(secondColor(3).getGreen() >> 3);
        c4b2.setValue(secondColor(3).getBlue() >> 3);
        c5r1.setValue(firstColor(4).getRed() >> 3);
        c5g1.setValue(firstColor(4).getGreen() >> 3);
        c5b1.setValue(firstColor(4).getBlue() >> 3);
        c5r2.setValue(secondColor(4).getRed() >> 3);
        c5g2.setValue(secondColor(4).getGreen() >> 3);
        c5b2.setValue(secondColor(4).getBlue() >> 3);
        updatingSpinners = false;
    }

    public void itemStateChanged(java.awt.event.ItemEvent e) {
	Object source = e.getItemSelectable();
	if (source == paletteSelector && e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            // Palette changed.
            if (paletteSelector.getSelectedIndex() != -1) {
                updatePreviewPanes();
                updateSpinners();
            }
        } else if (source == desaturateButton) {
	    this.desaturate = e.getStateChange() == java.awt.event.ItemEvent.SELECTED;
	    updatePreviewPanes();
	}
    }

    public void stateChanged(ChangeEvent e) {
        // Spinner changed.
        if (!updatingSpinners) {
            updateRomFromSpinners();
            updatePreviewPanes();
        }
    }

    private void savePalette(String path) {

        Object selectedItem = paletteSelector.getSelectedItem();
        if (selectedItem == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Couldn't read the palette name.");
            return;
        }
        String paletteName = (String) selectedItem;
        assert paletteName.length() == 4;
        try {
            java.io.FileOutputStream f = new java.io.FileOutputStream(path);
            f.write(paletteName.charAt(0));
            f.write(paletteName.charAt(1));
            f.write(paletteName.charAt(2));
            f.write(paletteName.charAt(3));
            for (int i = selectedPaletteOffset(); i < selectedPaletteOffset() + RomUtilities.PALETTE_SIZE; ++i) {
                f.write(romImage[i]);
            }
            f.close();
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Save failed!");
        }
    }

    private void loadPalette(java.io.File file) {
        String name = "";
        try {
            java.io.RandomAccessFile f = new java.io.RandomAccessFile(file, "r");
            name += (char) f.read();
            name += (char) f.read();
            name += (char) f.read();
            name += (char) f.read();
            setPaletteName(paletteSelector.getSelectedIndex(), name);
            for (int i = selectedPaletteOffset(); i < selectedPaletteOffset() + RomUtilities.PALETTE_SIZE; ++i) {
                romImage[i] = (byte) f.read();
            }
            f.close();
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Load failed!");
        }
        int index = paletteSelector.getSelectedIndex();
        populatePaletteSelector();
        paletteSelector.setSelectedIndex(index);
    }

    private void showOpenDialog() {
        JFileChooser chooser = JFileChooserFactory.createChooser("Load Palette", FileType.Lsdpal, FileOperation.Load);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            JFileChooserFactory.setBaseFolder(f.getParent());
            loadPalette(f);
        }
    }

    private void showSaveDialog() {
        JFileChooser chooser = JFileChooserFactory.createChooser("Save Palette", FileType.Lsdpal, FileOperation.Save);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            JFileChooserFactory.setBaseFolder(f.getParent());
            String filename = f.toString();
            if (!filename.endsWith("lsdpal")) {
                filename += ".lsdpal";
            }
            savePalette(filename);
        }
    }

    java.io.File clipboard;

    private void copyPalette() {
	    try {
		    clipboard = java.io.File.createTempFile("lsdpatcher", "palette");
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
	    savePalette(clipboard.getAbsolutePath());
	    mntmPaste.setEnabled(true);
    }

    private boolean areDuplicateNames() {
	    int paletteCount = RomUtilities.getNumberOfPalettes(romImage);
	    for (int i = 0; i < paletteCount; ++i) {
		    for (int j = i + 1; j < paletteCount; ++j) {
			    if (paletteName(i).equals(paletteName(j))) {
				    return true;
			    }
		    }
	    }
	    return false;
    }

    private void addNumberToPaletteName(int paletteIndex) {
	    char[] name = paletteName(paletteIndex).toCharArray();
	    char lastChar = name[name.length - 1];
	    if (Character.isDigit(lastChar)) {
		    ++lastChar;
	    } else {
		    lastChar = '1';
	    }
	    name[name.length - 1] = lastChar;
	    setPaletteName(paletteIndex, new String(name));
    }

	private void pastePalette() {
		int paletteIndex = paletteSelector.getSelectedIndex();
		loadPalette(clipboard);
		while (areDuplicateNames()) {
			addNumberToPaletteName(paletteIndex);
		}
		populatePaletteSelector();
		paletteSelector.setSelectedIndex(paletteIndex);
	}

    public void actionPerformed(java.awt.event.ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Open...":
                showOpenDialog();
                break;
            case "Save...":
                showSaveDialog();
                break;
            case "comboBoxChanged": {
                JComboBox cb = (JComboBox) e.getSource();
                if (cb.getSelectedIndex() != -1) {
                    previousSelectedPalette = cb.getSelectedIndex();
                }
                break;
            }
            case "comboBoxEdited": {
                // Kit name was edited.
                JComboBox cb = (JComboBox) e.getSource();
                if (cb.getSelectedIndex() == -1) {
                    setPaletteName(previousSelectedPalette, (String) cb.getSelectedItem());
                    if (!populatingPaletteSelector) {
                        populatePaletteSelector();
                        cb.setSelectedIndex(previousSelectedPalette);
                    }
                } else {
                    previousSelectedPalette = cb.getSelectedIndex();
                }
                break;
            }
	    case "Copy Palette":
		copyPalette();
		break;
	    case "Paste Palette":
		pastePalette();
		break;
	    default:
		assert false;
        }
    }
}
