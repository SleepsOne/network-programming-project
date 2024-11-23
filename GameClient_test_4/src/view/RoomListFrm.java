/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Client;

import java.io.IOException;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * @author Admin
 */
public class RoomListFrm extends javax.swing.JFrame {
    private Vector<String> listRoom;
    private Vector<String> listPassword;
    private boolean isPlayThread;
    private final boolean isFiltered;
    DefaultTableModel defaultTableModel;

    /**
     * Creates new form RoomListFrm
     */
    public RoomListFrm() {
        initComponents();
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        String[] columns = {"Room name", "Status"};
        DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        roomTextArea.setModel(newModel);
        defaultTableModel = (DefaultTableModel) roomTextArea.getModel();
        isPlayThread = true;
        isFiltered = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (Client.roomListFrm.isDisplayable() && isPlayThread && !isFiltered) {
                    try {
                        Client.socketHandle.write("view-room-list,");
                        Thread.sleep(500);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(rootPane, ex.getMessage());
                    }
                }
            }
        };
        thread.start();
    }

    public void updateRoomList(Vector<String> listData, Vector<String> listPassword) {
        this.listRoom = listData;
        this.listPassword = listPassword;
        defaultTableModel.setRowCount(0);
//        ImageIcon imageIcon;
        String state;
        for (int i = 0; i < listRoom.size(); i++) {
            if (listPassword.get(i).equals(" "))
//                imageIcon = new ImageIcon("assets/icon/swords-1-mini.png");
                state = "Join";
            else
//                imageIcon = new ImageIcon("assets/icon/swords-1-lock-mini.png");
                state = "Locked";
            defaultTableModel.addRow(new Object[]{
                    listRoom.get(i),
                    state
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        frameLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Object[][] rows = {
        };
        String[] columns = {"Tên phòng",""};
        DefaultTableModel model = new DefaultTableModel(rows, columns){
            @Override
            public Class<?> getColumnClass(int column){
                switch(column){
                    case 0: return String.class;
                    case 1: return ImageIcon.class;
                    default: return Object.class;
                }
            }
        };
        roomTextArea = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(105, 172, 174));

        frameLabel.setBackground(new java.awt.Color(255, 255, 255));
        frameLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        frameLabel.setForeground(new java.awt.Color(255, 255, 255));
        frameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frameLabel.setText("Room list");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/button-icon-png-favpng-Qu52qjHZFm9qNNzVYBMx1YJJ2 (1).jpg"))); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(50, 50));
        jButton1.setMinimumSize(new java.awt.Dimension(50, 50));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        roomTextArea.setFont(new java.awt.Font("Tekton Pro", 1, 36)); // NOI18N
        roomTextArea.setModel(model);
        roomTextArea.setFillsViewportHeight(true);
        roomTextArea.setRowHeight(60);
        roomTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roomTextAreaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(roomTextArea);
        if (roomTextArea.getColumnModel().getColumnCount() > 0) {
            roomTextArea.getColumnModel().getColumn(0).setMinWidth(240);
            roomTextArea.getColumnModel().getColumn(0).setPreferredWidth(240);
            roomTextArea.getColumnModel().getColumn(0).setMaxWidth(240);
            roomTextArea.getColumnModel().getColumn(1).setMinWidth(120);
            roomTextArea.getColumnModel().getColumn(1).setPreferredWidth(120);
            roomTextArea.getColumnModel().getColumn(1).setMaxWidth(120);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(frameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Client.closeView(Client.View.ROOM_LIST);
        Client.openView(Client.View.HOMEPAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void roomTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomTextAreaMouseClicked
        if (roomTextArea.getSelectedRow() == -1) {
        } else {
            try {
                isPlayThread = false;
                int index = roomTextArea.getSelectedRow();
                int room = Integer.parseInt(listRoom.get(index).trim());
                String password = listPassword.get(index);
                if (password.equals(" ")) {
                    Client.socketHandle.write("join-room," + room);
                    Client.closeView(Client.View.ROOM_LIST);
                } else {
                    Client.closeView(Client.View.ROOM_LIST);
                    Client.openView(Client.View.JOIN_ROOM_PASSWORD, room, password);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage());
            }
        }
    }//GEN-LAST:event_roomTextAreaMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel frameLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable roomTextArea;
    // End of variables declaration//GEN-END:variables
}
