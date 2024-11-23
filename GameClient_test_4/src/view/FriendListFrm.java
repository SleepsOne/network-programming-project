/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.Client;

import java.io.IOException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import model.User;

/**
 * @author Admin
 */
public class FriendListFrm extends javax.swing.JFrame {

    private List<User> listFriend;
    private boolean isClicked;
    DefaultTableModel defaultTableModel;


    public FriendListFrm() {
        initComponents();
        // Tạo lại model với tên cột mới
        String[] columns = {"ID", "Nickname", "Status"};
        DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        friendTable.setModel(newModel);
        defaultTableModel = (DefaultTableModel) friendTable.getModel();

        // Code khác...
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        isClicked = false;
        requestUpdate();
        startThread();
    }

    public void stopAllThread() {
        isClicked = true;
    }

    public void startThread() {
        // Tự cập nhật trạng thái của người chơi
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (Client.friendListFrm.isDisplayable() && !isClicked) {
                    try {
                        System.out.println("Xem danh sách bạn bè đang chạy!");
                        requestUpdate();
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public void requestUpdate() {
        try {
            Client.socketHandle.write("view-friend-list,");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
    }

    public void updateFriendList(List<User> friends) {
        listFriend = friends;
        defaultTableModel.setRowCount(0);
//        ImageIcon icon;
        String state;
        for (User friend : listFriend) {
            if (!friend.isOnline()) {
//                icon = new ImageIcon("offline.png");
                state = "Offline";
            } else if (friend.isPlaying()) {
//                icon = new ImageIcon("");
                state = "Playing";
            } else {
//                icon = new ImageIcon("");
                state = "Online";
            }
            defaultTableModel.addRow(new Object[]{
                "" + friend.getID(),
                friend.getNickname(), state

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
        closeButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Object[][] rows = {
        };
        String[] columns = {"ID","Nickname",""};
        DefaultTableModel model = new DefaultTableModel(rows, columns){
            @Override
            public Class<?> getColumnClass(int column){
                switch(column){
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return ImageIcon.class;
                    default: return Object.class;
                }
            }
        };
        friendTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(105, 172, 174));

        frameLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        frameLabel.setForeground(new java.awt.Color(255, 255, 255));
        frameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frameLabel.setText("Friend list");

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/button-icon-png-favpng-Qu52qjHZFm9qNNzVYBMx1YJJ2 (1).jpg"))); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        friendTable.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        friendTable.setModel(model);
        friendTable.setRowHeight(60);
        friendTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                friendTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(friendTable);
        if (friendTable.getColumnModel().getColumnCount() > 0) {
            friendTable.getColumnModel().getColumn(0).setMinWidth(60);
            friendTable.getColumnModel().getColumn(0).setPreferredWidth(60);
            friendTable.getColumnModel().getColumn(0).setMaxWidth(60);
            friendTable.getColumnModel().getColumn(1).setMinWidth(240);
            friendTable.getColumnModel().getColumn(1).setPreferredWidth(240);
            friendTable.getColumnModel().getColumn(1).setMaxWidth(240);
            friendTable.getColumnModel().getColumn(2).setMinWidth(120);
            friendTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            friendTable.getColumnModel().getColumn(2).setMaxWidth(120);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(closeButton)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(frameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(closeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        Client.closeView(Client.View.FRIEND_LIST);
        Client.openView(Client.View.HOMEPAGE);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void friendTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendTableMouseClicked
        try {
            if (friendTable.getSelectedRow() == -1) {
                return;
            }
            User friend = listFriend.get(friendTable.getSelectedRow());
            if (!friend.isOnline()) {
                throw new Exception("Player is not online !");
            }
            if (friend.isPlaying()) {
                throw new Exception("Player in match !");
            }
            isClicked = true;
            int res = JOptionPane.showConfirmDialog(rootPane, "You want to battle this player ?", "", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                Client.closeAllViews();
                Client.openView(Client.View.GAME_NOTICE, "Battle request...", "Waiting for accept...");
                Client.socketHandle.write("duel-request," + friend.getID());
            } else {
                isClicked = false;
                startThread();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
    }//GEN-LAST:event_friendTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel frameLabel;
    private javax.swing.JTable friendTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
