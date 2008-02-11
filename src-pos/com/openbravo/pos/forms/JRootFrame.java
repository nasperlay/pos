//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007 Openbravo, S.L.
//    http://sourceforge.net/projects/openbravopos
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package com.openbravo.pos.forms;

import java.awt.BorderLayout;
import java.rmi.RemoteException;
import javax.swing.JFrame;
import com.openbravo.pos.instance.AppMessage;
import com.openbravo.pos.instance.InstanceManager;

/**
 *
 * @author  adrianromero
 */
public class JRootFrame extends javax.swing.JFrame implements AppMessage {
    
    // Gestor de que haya solo una instancia corriendo en cada maquina.
    private InstanceManager m_instmanager = null;
    
    private JRootApp m_rootapp;
    private AppProperties m_props;
    
    /** Creates new form JRootFrame */
    public JRootFrame() {
        
        initComponents();
    }
    
    public void initFrame(AppProperties props) {
        
        m_props = props;
        
        m_rootapp = new JRootApp();
        
        if (m_rootapp.initApp(m_props)) {
            
            // Register the running application
            try {
                m_instmanager = new InstanceManager(this);
            } catch (Exception e) {
            }
        
            // Show the application
            add(m_rootapp, BorderLayout.CENTER);            
 
            setTitle(AppLocal.APP_NAME + " - " + m_rootapp.getDataBaseVersion());
            pack();
            setLocationRelativeTo(null);        
            
            setVisible(true);                        
        } else {
            System.exit(1);
        }
    }
    
    public void restoreWindow() throws RemoteException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (getExtendedState() == JFrame.ICONIFIED) {
                    setExtendedState(JFrame.NORMAL);
                }
                requestFocus();
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(AppLocal.APP_NAME);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        m_rootapp.tryToClose();
        
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        System.exit(0);
        
    }//GEN-LAST:event_formWindowClosed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
