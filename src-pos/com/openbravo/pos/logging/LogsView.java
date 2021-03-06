package com.openbravo.pos.logging;

import com.openbravo.basic.BasicException;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author stas
 */
public class LogsView extends JPanel implements EditorRecord {

    private Object logId;

    LogsView(DataLogicLogs dlLogs) {
        initComponents();
        writeValueEOF();
    }

    void activate() {

    }

    @Override
    public void writeValueEOF() {
        logId = null;
        m_jName.setText(null);
        m_jText.setText(null);
        m_jName.setEnabled(true);
        m_jText.setEnabled(true);
        m_jName.setEditable(false);
        m_jText.setEditable(false);
    }

    @Override
    public void writeValueInsert() {
        logId = null;
        m_jName.setText(null);
        m_jText.setText(null);
        m_jName.setEnabled(true);
        m_jText.setEnabled(true);
        m_jName.setEditable(false);
        m_jText.setEditable(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] log = (Object[]) value;
        m_jName.setText(Formats.STRING.formatValue(log[1]));
        m_jText.setText(Formats.STRING.formatValue(log[3]));
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] log = (Object[]) value;
        m_jName.setText(Formats.STRING.formatValue(log[1]));
        m_jText.setText(Formats.STRING.formatValue(log[3]));
    }

    @Override
    public void refresh() {}

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
        return null;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        m_jText = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();

        m_jText.setWrapStyleWord(false);
        m_jText.setFont(new java.awt.Font("DialogInput", 0, 12));
        m_jText.setSelectionColor(new java.awt.Color(51, 153, 255));
        jScrollPane1.setViewportView(m_jText);

        jLabel2.setText(AppLocal.getIntString("label.component")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField m_jName;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea m_jText;
    // End of variables declaration//GEN-END:variables
}
