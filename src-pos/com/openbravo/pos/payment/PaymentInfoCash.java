//    Upsources POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Upsources POS.
//
//    Upsources POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Upsources POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Upsources POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;

import com.openbravo.format.Formats;
import java.text.DecimalFormat;

public class PaymentInfoCash extends PaymentInfo {
    //Número do cheque
    private String m_dChequeNo;
    //Nome do Banco
    private Object m_dBankName;
    //Data do Cheque
    private String m_dChequeDate;
    private double m_dPaid;
    private double m_dTotal;
    
    /** Creates a new instance of PaymentInfoCash */
    public PaymentInfoCash(double dTotal, double dPaid) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
    }
    
    public PaymentInfo copyPayment(){
        return new PaymentInfoCash(m_dTotal, m_dPaid);
    }
    
    public String getName() {
        return "cash";
    }   
    public double getTotal() {
        return m_dTotal;
    }   
    public double getPaid() {
        return m_dPaid;
    }
    public String getTransactionID(){
        return "no ID";
    }
    //Número do cheque
    public String getChequeNumber() {
        return m_dChequeNo;
    }
    //Nome do Banco
    public Object getBankName() {
        return m_dBankName;
    }
    //Data do Cheque
    public String getChequeDate() {
        return m_dChequeDate;
    }
    public String printPaid() {
        return Formats.CURRENCY.formatValue(new Double(m_dPaid));
    }   
    public String printChange() {
        return Formats.CURRENCY.formatValue(new Double(m_dPaid - m_dTotal));
    }
    
    public String printPaidFPrint() {
        DecimalFormat nf = new DecimalFormat("##########.##");
        nf.setMinimumFractionDigits(2);
        return nf.format(new Double(m_dPaid)).replace(",", ".");
    }
}
