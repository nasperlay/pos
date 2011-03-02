//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2008 Openbravo, S.L.
//    http://sourceforge.net/projects/openbravopos
//
//    This file is modified as part of fastfood branch of Openbravo POS. 2008
//    These modifications are copyright Open Sistemas de Información Internet, S.L.
//    http://www.opensistemas.com
//    e-mail: imasd@opensistemas.com
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

package com.openbravo.pos.catalog;

import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.util.ThumbNailBuilder;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.ticket.DiscountInfo;

/**
 *
 * @author adrianromero
 * Modified by:
 * @author  Luis Ig. Bacas Riveiro	lbacas@opensistemas.com
 * @author  Pablo J. Urbano Santos	purbano@opensistemas.com
 */
public class JCatalog extends JPanel implements ListSelectionListener, CatalogSelector {
    
    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;   
    private boolean pricevisible;
    private boolean taxesincluded;
    private boolean showDiscounts;
    
    // Set of Products panels
    private Map<String, ProductInfoExt> m_productsset = new HashMap<String, ProductInfoExt>();
    
    // Set of Categoriespanels
     private Set<String> m_categoriesset = new HashSet<String>();
        
    private ThumbNailBuilder tnbbutton;
    private ThumbNailBuilder tnbcat;
    
    private CategoryInfo showingcategory = null;
    
    /** Creates new form JCatalog */
    public JCatalog(DataLogicSales dlSales) {
        this(dlSales, false, false, 64, 54);
    }
    
    public JCatalog(DataLogicSales dlSales, boolean pricevisible, boolean taxesincluded, int width, int height) {
        
        m_dlSales = dlSales;
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        showDiscounts = false;
        
        initComponents();
        
        m_jListCategories.addListSelectionListener(this);                
        m_jscrollcat.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        
        tnbcat = new ThumbNailBuilder(32, 32, "com/openbravo/images/folder_yellow.png");           
        tnbbutton = new ThumbNailBuilder(width, height, "com/openbravo/images/package.png");
    }
    
    public Component getComponent() {
        return this;
    }
    
    public void showCatalogPanel(String id) {
           
        if (id == null) {
            showRootCategoriesPanel();
        } else {            
            showProductPanel(id);
        }
    }
    
    public void showDiscounts (boolean bValue) {
        showDiscounts = bValue;
    }
    
    public void loadCatalog() throws BasicException {
        
        // delete all categories panel
        m_jProducts.removeAll();
        
        m_productsset.clear();        
        m_categoriesset.clear();
        
        showingcategory = null;

        // Load all categories.
        java.util.List<CategoryInfo> categories = m_dlSales.getRootCategories(); 
        
        // Si la categoria Composiciones esta vacia, no mostrarla
        java.util.List<ProductInfoExt> compositions = m_dlSales.getProductCatalog("0");
        if (compositions.size() == 0) {
            for (CategoryInfo c: categories) {
                if (c.getID().equals("0")) {
                    categories.remove(c);
                    break;
                }
            }
        }
        
        // Add discount category
        java.util.List<DiscountInfo> discounts = (m_dlSales.getDiscountList()).list();
        
        if (showDiscounts && discounts.size() > 0) {
            CategoryInfo discountCat = new CategoryInfo();
            discountCat.setID(AppLocal.getIntString("Menu.Discounts"));
            discountCat.setName(AppLocal.getIntString("Menu.Discounts"));
            discountCat.setImage(null);
            categories.add(discountCat);
        }
        
        // Select the first category
        m_jListCategories.setCellRenderer(new SmallCategoryRenderer());
        m_jListCategories.setModel(new CategoriesListModel(categories));

         if (m_jListCategories.getModel().getSize() == 0) {
             m_jscrollcat.setVisible(false);
             jPanel2.setVisible(false);
         } else {
             m_jscrollcat.setVisible(true);
             jPanel2.setVisible(true);
            m_jListCategories.setSelectedIndex(0);
         }
            
        // Display catalog panel
        showRootCategoriesPanel();
    }

    public void setComponentEnabled(boolean value) {
        
        m_jListCategories.setEnabled(value);
        m_jscrollcat.setEnabled(value);
        m_jUp.setEnabled(value);
        m_jDown.setEnabled(value);
        m_lblIndicator.setEnabled(value);
        m_btnBack.setEnabled(value);
        m_jProducts.setEnabled(value); 
        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0 ; i < compCount ; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }
     
        this.setEnabled(value);
    }
    
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    public void valueChanged(ListSelectionEvent evt) {
        
        if (!evt.getValueIsAdjusting()) {
            int i = m_jListCategories.getSelectedIndex();
            if (i >= 0) {
                // Lo hago visible...
                Rectangle oRect = m_jListCategories.getCellBounds(i, i);
                m_jListCategories.scrollRectToVisible(oRect);       
            }
        }
    }  
    
    protected void fireSelectedProduct(ProductInfoExt prod) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l[i]).actionPerformed(e);	       
        }
    }   
    protected void fireSelectedDiscount(DiscountInfo d) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(d, ActionEvent.ACTION_PERFORMED, d.getID());
            }
            ((ActionListener) l[i]).actionPerformed(e);	       
        }
    }   
        
    
    private void selectCategoryPanel(String catid) {

        try {
            // Load categories panel if not exists
            if (!m_categoriesset.contains(catid)) {
                
                JCatalogTab jcurrTab = new JCatalogTab();      
                m_jProducts.add(jcurrTab, catid);
                m_categoriesset.add(catid);
                
                if (showDiscounts && catid.equals(AppLocal.getIntString("Menu.Discounts")) ) {
                    // Add discounts
                    java.util.List<DiscountInfo> discounts = (m_dlSales.getDiscountList()).list();
                    for (DiscountInfo d : discounts) {
                        jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(null, d.getName())), new SelectedAction(d));
                    }
                } else {
                    // Add subcategories
                    java.util.List<CategoryInfo> categories = m_dlSales.getSubcategories(catid);
                    for (CategoryInfo cat : categories) {
                        jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(cat.getImage(), cat.getName())), new SelectedCategory(cat));
                    }

                    // Add products
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductCatalog(catid);
                    for (ProductInfoExt prod : products) {
                        jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                    }
                }
            }
            
            // Show categories panel
            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, catid);  
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));            
        }
    }
    
    private String getProductLabel(ProductInfoExt product) {

        if (pricevisible) {
            if (taxesincluded) {
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSellTax();
            } else {
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSell();
            }
        } else {
            return product.getName();
        }
    }
    
    private void selectIndicatorPanel(Icon icon, String label) {
        
        m_lblIndicator.setText(label);
        m_lblIndicator.setIcon(icon);
        
        // Show subcategories panel
        CardLayout cl = (CardLayout)(m_jCategories.getLayout());
        cl.show(m_jCategories, "subcategories");
    }
    
    private void selectIndicatorCategories() {
        // Show root categories panel
        CardLayout cl = (CardLayout)(m_jCategories.getLayout());
        cl.show(m_jCategories, "rootcategories");
    }
    
    private void showRootCategoriesPanel() {
        
        selectIndicatorCategories();
        // Show selected root category
        CategoryInfo cat = (CategoryInfo) m_jListCategories.getSelectedValue();
        if (cat != null) {
            selectCategoryPanel(cat.getID());
        }
        showingcategory = null;
    }
    
    private void showSubcategoryPanel(CategoryInfo category) {
        
        selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(category.getImage())), category.getName());
        selectCategoryPanel(category.getID());
        showingcategory = category;
    }
    
    private void showProductPanel(String id) {
            
        ProductInfoExt product = m_productsset.get(id);

        if (product == null) {
            if (m_productsset.containsKey(id)) {
                // It is an empty panel
                if (showingcategory == null) {
                    showRootCategoriesPanel();                         
                } else {
                    showSubcategoryPanel(showingcategory);
                }
            } else {
                try {
                    // Create  products panel
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductComments(id);

                    if (products.size() == 0) {
                        // no hay productos por tanto lo anado a la de vacios y muestro el panel principal.
                        m_productsset.put(id, null);
                        if (showingcategory == null) {
                            showRootCategoriesPanel();                         
                        } else {
                            showSubcategoryPanel(showingcategory);
                        }
                    } else {

                        // Load product panel
                        product = m_dlSales.getProductInfo(id);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();      
                        m_jProducts.add(jcurrTab, "PRODUCT." + id);                        

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                        }                       

                        selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(product.getImage())), product.getName());

                        CardLayout cl = (CardLayout)(m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id); 
                    }
                } catch (BasicException eb) {
                    eb.printStackTrace();
                    m_productsset.put(id, null);
                    if (showingcategory == null) {
                        showRootCategoriesPanel();                         
                    } else {
                        showSubcategoryPanel(showingcategory);
                    }
                }
            }
        } else {
            // already exists
            selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(product.getImage())), product.getName());

            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id); 
        }
    }
    
    private class SelectedAction implements ActionListener {
        private ProductInfoExt prod;
        private DiscountInfo disc;
        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }
        public SelectedAction(DiscountInfo disc) {
            this.disc = disc;
        }
        public void actionPerformed(ActionEvent e) {
            if (prod != null)       fireSelectedProduct(prod);
            else if (disc != null)  fireSelectedDiscount(disc);            
        }
    }
    
    private class SelectedCategory implements ActionListener {
        private CategoryInfo category;
        public SelectedCategory(CategoryInfo category) {
            this.category = category;
        }
        public void actionPerformed(ActionEvent e) {
            showSubcategoryPanel(category);
        }
    }
    
    private class CategoriesListModel extends AbstractListModel {
        private java.util.List m_aCategories;
        public CategoriesListModel(java.util.List aCategories) {
            m_aCategories = aCategories;
        }
        public int getSize() { 
            return m_aCategories.size(); 
        }
        public Object getElementAt(int i) {
            return m_aCategories.get(i);
        }
    }
    
    private class SmallCategoryRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
            CategoryInfo cat = (CategoryInfo) value;
            setText(cat.getName());
            setIcon(new ImageIcon(tnbcat.getThumbNail(cat.getImage())));
            return this;
        }      
    }            
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jCategories = new javax.swing.JPanel();
        m_jRootCategories = new javax.swing.JPanel();
        m_jscrollcat = new javax.swing.JScrollPane();
        m_jListCategories = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        m_jUp = new javax.swing.JButton();
        m_jDown = new javax.swing.JButton();
        m_jSubCategories = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_lblIndicator = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        m_btnBack = new javax.swing.JButton();
        m_jProducts = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        m_jCategories.setLayout(new java.awt.CardLayout());

        m_jRootCategories.setLayout(new java.awt.BorderLayout());

        m_jscrollcat.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        m_jscrollcat.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        m_jscrollcat.setPreferredSize(new java.awt.Dimension(210, 0));

        m_jListCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        m_jListCategories.setFocusable(false);
        m_jListCategories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                m_jListCategoriesValueChanged(evt);
            }
        });
        m_jscrollcat.setViewportView(m_jListCategories);

        m_jRootCategories.add(m_jscrollcat, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel3.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        m_jUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow22.png"))); // NOI18N
        m_jUp.setFocusPainted(false);
        m_jUp.setFocusable(false);
        m_jUp.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jUp.setRequestFocusEnabled(false);
        m_jUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jUpActionPerformed(evt);
            }
        });
        jPanel3.add(m_jUp);

        m_jDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow22.png"))); // NOI18N
        m_jDown.setFocusPainted(false);
        m_jDown.setFocusable(false);
        m_jDown.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDown.setRequestFocusEnabled(false);
        m_jDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDownActionPerformed(evt);
            }
        });
        jPanel3.add(m_jDown);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        m_jRootCategories.add(jPanel2, java.awt.BorderLayout.CENTER);

        m_jCategories.add(m_jRootCategories, "rootcategories");

        m_jSubCategories.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        m_lblIndicator.setText("jLabel1");
        jPanel4.add(m_lblIndicator, java.awt.BorderLayout.NORTH);

        m_jSubCategories.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        m_btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/3uparrow.png"))); // NOI18N
        m_btnBack.setFocusPainted(false);
        m_btnBack.setFocusable(false);
        m_btnBack.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnBack.setRequestFocusEnabled(false);
        m_btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnBackActionPerformed(evt);
            }
        });
        jPanel5.add(m_btnBack);

        jPanel1.add(jPanel5, java.awt.BorderLayout.NORTH);

        m_jSubCategories.add(jPanel1, java.awt.BorderLayout.EAST);

        m_jCategories.add(m_jSubCategories, "subcategories");

        add(m_jCategories, java.awt.BorderLayout.WEST);

        m_jProducts.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnBackActionPerformed

        showRootCategoriesPanel();        
        
    }//GEN-LAST:event_m_btnBackActionPerformed

    private void m_jDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDownActionPerformed

        int i = m_jListCategories.getSelectionModel().getMaxSelectionIndex();
        if (i < 0){
            i =  0; // No hay ninguna seleccionada
        } else {
            i ++;
            if (i >= m_jListCategories.getModel().getSize() ) {
                i = m_jListCategories.getModel().getSize() - 1;
            }
        }

        if ((i >= 0) && (i < m_jListCategories.getModel().getSize())) {
            // Solo seleccionamos si podemos.
            m_jListCategories.getSelectionModel().setSelectionInterval(i, i);
        }        
        
    }//GEN-LAST:event_m_jDownActionPerformed

    private void m_jUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jUpActionPerformed

        int i = m_jListCategories.getSelectionModel().getMinSelectionIndex();
        if (i < 0){
            i = m_jListCategories.getModel().getSize() - 1; // No hay ninguna seleccionada
        } else {
            i --;
            if (i < 0) {
                i = 0;
            }
        }

        if ((i >= 0) && (i < m_jListCategories.getModel().getSize())) {
            // Solo seleccionamos si podemos.
            m_jListCategories.getSelectionModel().setSelectionInterval(i, i);
        }        
        
        
    }//GEN-LAST:event_m_jUpActionPerformed

    private void m_jListCategoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_m_jListCategoriesValueChanged

        if (!evt.getValueIsAdjusting()) {
            CategoryInfo cat = (CategoryInfo) m_jListCategories.getSelectedValue();
            if (cat != null) {
                selectCategoryPanel(cat.getID());
            }
        }
        
    }//GEN-LAST:event_m_jListCategoriesValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_btnBack;
    private javax.swing.JPanel m_jCategories;
    private javax.swing.JButton m_jDown;
    private javax.swing.JList m_jListCategories;
    private javax.swing.JPanel m_jProducts;
    private javax.swing.JPanel m_jRootCategories;
    private javax.swing.JPanel m_jSubCategories;
    private javax.swing.JButton m_jUp;
    private javax.swing.JScrollPane m_jscrollcat;
    private javax.swing.JLabel m_lblIndicator;
    // End of variables declaration//GEN-END:variables
    
}
