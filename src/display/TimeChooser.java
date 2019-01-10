package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import com.toedter.components.JSpinField;

@SuppressWarnings("serial")
public class TimeChooser extends JWindow {
	TimeChooser me = this;
	JSpinField  spinnerDay;
	JMonthChooser  spinnerMonth;
	JYearChooser  spinnerYear;
	JSpinField spinnerHour;
	JSpinField spinnerMinutes;
	JSpinField spinnerSeconds;
	SimpleDateFormat formater;
	public TimeChooser(JButton button) {
		JPanel pan = new JPanel();
	    pan.setBorder(new LineBorder(Color.black));
	    getContentPane().add(pan,"Center");
		pan.setLayout(null);
		setSize(225,120);
		pan.setSize(240,130);
		//dateChooser.addComponentListener(l);
		JLabel lblDay = new JLabel("Jour",SwingConstants.CENTER);
		JLabel lblMonth = new JLabel("Mois",SwingConstants.CENTER);
		JLabel lblYear = new JLabel("Annee",SwingConstants.CENTER);
		JLabel lblHours = new JLabel("Heures",SwingConstants.CENTER);
		JLabel lblMinutes = new JLabel("Minutes",SwingConstants.CENTER);
		JLabel lblSeconds = new JLabel("Secondes",SwingConstants.CENTER);
		

		spinnerDay = new JSpinField(1,31);
		spinnerMonth = new JMonthChooser();
		spinnerYear = new JYearChooser();
		spinnerHour = new JSpinField(0,23);
		spinnerMinutes = new JSpinField(0,59);
		spinnerSeconds = new JSpinField(0,59);
		lblDay.setBounds(5,5,50,15);
		spinnerDay.setBounds(5,25,50,20);
		lblMonth.setBounds(60,5,110,15);
		spinnerMonth.setBounds(60,25,110,20);
		lblYear.setBounds(170,5,50,15);
		spinnerYear.setBounds(170,25,50,20);
		
		lblHours.setBounds(25,50,50,20);
		spinnerHour.setBounds(30,75,50,20);
		lblMinutes.setBounds(80,50,50,20);
		spinnerMinutes.setBounds(85,75,50,20);
		lblSeconds.setBounds(130,50,60,20);
		spinnerSeconds.setBounds(140,75,50,20);
		
		pan.add(lblDay);
		pan.add(spinnerDay);
		pan.add(lblMonth);
		pan.add(spinnerMonth);
		pan.add(lblYear);
		pan.add(spinnerYear);
		pan.add(lblHours);
		pan.add(spinnerHour);
		pan.add(lblMinutes);
		pan.add(spinnerMinutes);
		pan.add(lblSeconds);
		pan.add(spinnerSeconds);

		setAction(button);
	}

	private void setAction(JButton b) {
		b.addActionListener(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(me.isVisible()) {
					Date date = getDate();
					if(date == null) {
						JOptionPane.showMessageDialog(null, "Entrez une date/heure valide.");
						me.setVisible(true);
					}
					else {
						me.setVisible(false);
						formater = null;
						formater = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");
						b.setText(formater.format(getDate()));
						b.setFont(new Font("Verdana", Font.PLAIN, 12));
					}
				}else {
					me.show(b, b.getWidth(), b.getHeight());
				}
			}
		});
	}

	// Shows the window under the button which calls it.
	public void show(JButton button, int x, int y) {
		Point p = button.getLocationOnScreen();
		p.x += x - getWidth()*0.75;
		p.y += y;
		setLocation(p);
		setVisible(true);
	}
	
	@SuppressWarnings("deprecation")
	public Date getDate() {
		int year = spinnerYear.getValue();
		int month = spinnerMonth.getMonth();
		int days = spinnerDay.getValue();
		int hours = spinnerHour.getValue();
		int minutes = spinnerMinutes.getValue();
		int seconds = spinnerSeconds.getValue();
		if(year <= 0 || month < 0 || month > 11 || days < 0 || days > 31 || hours < 0 || hours > 23 || minutes < 0 || minutes > 60 || seconds < 0 || seconds > 60) {
			return null;
		}
		return new Date(year - 1900,month,days,hours,minutes,seconds);
	}

	@SuppressWarnings("deprecation")
	public void setDate(Date newDate) {
		spinnerYear.setValue(newDate.getYear());
		spinnerMonth.setMonth(newDate.getMonth());
		spinnerDay.setValue(newDate.getDay());
		spinnerHour.setValue(newDate.getHours());
		spinnerMinutes.setValue(newDate.getMinutes());
		spinnerSeconds.setValue(newDate.getSeconds());
	}
	
}
