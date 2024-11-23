package view;

import controller.Client;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.User;

public class HomePageFrm extends javax.swing.JFrame {

    public HomePageFrm() {
        initComponents();
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        jLabel4.setText(Client.user.getNickname());
        numberOfWinValue.setText(Integer.toString(Client.user.getNumberOfWin()));
        numberOfGameValue.setText(Integer.toString(Client.user.getNumberOfGame()));

        markValue.setText("" + Client.user.getScore());
        rankValue.setText("" + Client.user.getRank());
    }

    public void updateUserInfo(User user) {
        numberOfWinValue.setText(Integer.toString(user.getNumberOfWin()));
        numberOfGameValue.setText(Integer.toString(user.getNumberOfGame()));

        markValue.setText("" + user.getScore());
        rankValue.setText("" + user.getRank());

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        frameLabel = new javax.swing.JLabel();
        createRoomButton = new javax.swing.JButton();
        scoreBoardButton = new javax.swing.JButton();
        findRoomButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        numberOfWinLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        numberOfWinValue = new javax.swing.JLabel();
        numberOfGameValue = new javax.swing.JLabel();
        numberOfGameLabel = new javax.swing.JLabel();
        markLabel = new javax.swing.JLabel();
        markValue = new javax.swing.JLabel();
        rankLabel = new javax.swing.JLabel();
        rankValue = new javax.swing.JLabel();
        scoreBotButton = new javax.swing.JButton();
        exitGameButton = new javax.swing.JButton();
        quickGameButton = new javax.swing.JButton();
        friendListButton = new javax.swing.JButton();
        goRoomButton = new javax.swing.JButton();
        history_btn = new javax.swing.JButton();

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        frameLabel.setFont(new java.awt.Font("Bookman Old Style", 1, 24)); // NOI18N
        frameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frameLabel.setText("Quick Math");

        createRoomButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        createRoomButton.setText("Create room");
        createRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRoomButtonActionPerformed(evt);
            }
        });

        scoreBoardButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        scoreBoardButton.setText("Rank");
        scoreBoardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreBoardButtonActionPerformed(evt);
            }
        });

        findRoomButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        findRoomButton.setText("Find room");
        findRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findRoomButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(105, 172, 174));

        jLabel1.setFont(new java.awt.Font("Sylfaen", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("NickName");
        jLabel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                jLabel1AncestorMoved(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        numberOfWinLabel.setFont(new java.awt.Font("Sylfaen", 0, 12)); // NOI18N
        numberOfWinLabel.setForeground(new java.awt.Color(255, 255, 255));
        numberOfWinLabel.setText("Win");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("{day la Nick name}");

        numberOfWinValue.setBackground(new java.awt.Color(0, 0, 0));
        numberOfWinValue.setForeground(new java.awt.Color(255, 255, 255));
        numberOfWinValue.setText("{day la so van thang}");

        numberOfGameValue.setForeground(new java.awt.Color(255, 255, 255));
        numberOfGameValue.setText("{day la so van da choi}");

        numberOfGameLabel.setFont(new java.awt.Font("Sylfaen", 0, 12)); // NOI18N
        numberOfGameLabel.setForeground(new java.awt.Color(255, 255, 255));
        numberOfGameLabel.setText("No. Games");

        markLabel.setFont(new java.awt.Font("Sylfaen", 0, 12)); // NOI18N
        markLabel.setForeground(new java.awt.Color(255, 255, 255));
        markLabel.setText("Score");

        markValue.setForeground(new java.awt.Color(255, 255, 255));
        markValue.setText("{day la diem}");

        rankLabel.setFont(new java.awt.Font("Sylfaen", 0, 12)); // NOI18N
        rankLabel.setForeground(new java.awt.Color(255, 255, 255));
        rankLabel.setText("Rank");

        rankValue.setForeground(new java.awt.Color(255, 255, 255));
        rankValue.setText("{day la thu hang}");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberOfGameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(numberOfGameValue, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberOfWinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberOfWinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(markLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(markValue, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rankLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(rankValue, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(numberOfGameLabel)
                    .addComponent(numberOfWinLabel)
                    .addComponent(markLabel)
                    .addComponent(rankLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(numberOfGameValue)
                    .addComponent(numberOfWinValue)
                    .addComponent(markValue)
                    .addComponent(rankValue))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        scoreBotButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        scoreBotButton.setText("Log out");
        scoreBotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreBotButtonActionPerformed(evt);
            }
        });

        exitGameButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        exitGameButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/exit-png-new-svg-image-2000 (1) (1).png"))); // NOI18N
        exitGameButton.setText("Tired ? Have a nap time");
        exitGameButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        exitGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitGameButtonActionPerformed(evt);
            }
        });

        quickGameButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        quickGameButton.setText("Quick match");
        quickGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickGameButtonActionPerformed(evt);
            }
        });

        friendListButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        friendListButton.setText("Friends");
        friendListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                friendListButtonActionPerformed(evt);
            }
        });

        goRoomButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        goRoomButton.setText("Join room");
        goRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goRoomButtonActionPerformed(evt);
            }
        });

        history_btn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        history_btn.setText("Match History");
        history_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                history_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(frameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(exitGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(history_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(goRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scoreBoardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scoreBotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quickGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(friendListButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameLabel)
                    .addComponent(goRoomButton)
                    .addComponent(quickGameButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createRoomButton)
                            .addComponent(findRoomButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scoreBoardButton)
                            .addComponent(friendListButton)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(scoreBotButton)
                        .addComponent(history_btn))
                    .addComponent(exitGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1AncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jLabel1AncestorMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1AncestorMoved

    private void createRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoomButtonActionPerformed
        int res = JOptionPane.showConfirmDialog(rootPane, "Do you want to set a password for the room?", "Create room", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            Client.closeView(Client.View.HOMEPAGE);
            Client.openView(Client.View.CREATE_ROOM_PASSWORD);
        } else if (res == JOptionPane.NO_OPTION) {
            try {
                Client.socketHandle.write("create-room,");
                Client.closeView(Client.View.HOMEPAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage());
            }
        }
    }//GEN-LAST:event_createRoomButtonActionPerformed

    private void findRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findRoomButtonActionPerformed
        try {
            Client.closeView(Client.View.HOMEPAGE);
            Client.openView(Client.View.ROOM_LIST);
            Client.socketHandle.write("view-room-list,");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
    }//GEN-LAST:event_findRoomButtonActionPerformed

    private void scoreBoardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreBoardButtonActionPerformed
        Client.openView(Client.View.RANK);
    }//GEN-LAST:event_scoreBoardButtonActionPerformed

    private void scoreBotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreBotButtonActionPerformed
        try {
            Client.socketHandle.write("offline," + Client.user.getID());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage());
        }
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.LOGIN);
    }//GEN-LAST:event_scoreBotButtonActionPerformed

    private void exitGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitGameButtonActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_exitGameButtonActionPerformed

    private void friendListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_friendListButtonActionPerformed
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.FRIEND_LIST);
    }//GEN-LAST:event_friendListButtonActionPerformed

    private void quickGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quickGameButtonActionPerformed
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.FIND_ROOM);
    }//GEN-LAST:event_quickGameButtonActionPerformed

    private void goRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goRoomButtonActionPerformed
        Client.openView(Client.View.ROOM_NAME_FRM);
    }//GEN-LAST:event_goRoomButtonActionPerformed

    private void history_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_history_btnActionPerformed
        // TODO add your handling code here:

        if (Client.matchHistoryFrm == null) {
            Client.matchHistoryFrm = new MatchHistoryFrm();
        }
        Client.matchHistoryFrm.setVisible(true);

    }//GEN-LAST:event_history_btnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createRoomButton;
    private javax.swing.JButton exitGameButton;
    private javax.swing.JButton findRoomButton;
    private javax.swing.JLabel frameLabel;
    private javax.swing.JButton friendListButton;
    private javax.swing.JButton goRoomButton;
    private javax.swing.JButton history_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel markLabel;
    private javax.swing.JLabel markValue;
    private javax.swing.JLabel numberOfGameLabel;
    private javax.swing.JLabel numberOfGameValue;
    private javax.swing.JLabel numberOfWinLabel;
    private javax.swing.JLabel numberOfWinValue;
    private javax.swing.JButton quickGameButton;
    private javax.swing.JLabel rankLabel;
    private javax.swing.JLabel rankValue;
    private javax.swing.JButton scoreBoardButton;
    private javax.swing.JButton scoreBotButton;
    // End of variables declaration//GEN-END:variables
}
