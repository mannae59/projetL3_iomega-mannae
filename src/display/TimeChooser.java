package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
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
	public TimeChooser(JToggleButton button, TimeChooser t) {
		JPanel pan = new JPanel();
	    pan.setBorder(new LineBorder(Color.black));
	    getContentPane().add(pan,"Center");
		pan.setLayout(null);
		setSize(225,120);
		pan.setSize(240,130);
		JLabel lblDay = new JLabel("Jour",SwingConstants.CENTER);
		JLabel lblMonth = new JLabel("Mois",SwingConstants.CENTER);
		JLabel lblYear = new JLabel("Annee",SwingConstants.CENTER);
		JLabel lblHours = new JLabel("Heures",SwingConstants.CENTER);
		JLabel lblMinutes = new JLabel("Minutes",SwingConstants.CENTER);
		JLabel lblSeconds = new JLabel("Secondes",SwingConstants.CENTER);
		

		spinnerDay = new JSpinField(1,31);
		spinnerDay.setValue(1);
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

		setAction(button,t);
	}
	// Shows the panel if closed, sets the date if panel opened
	// (and sets the date for another TimeChooser t)
	private void setAction(JToggleButton b, TimeChooser t) {
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
						if(t != null) t.setDate(getDate());
					}
				}else {
					me.show(b, b.getWidth(), b.getHeight());
				}
			}
		});
	}

	// Shows the window under the button which calls it.
	public void show(JToggleButton button, int x, int y) {
		Point p = button.getLocationOnScreen();
		p.x += x - getWidth()*0.75;
		p.y += y;
		setLocation(p);
		setVisible(true);
	}
	
	public Date getDate() {
		int year = spinnerYear.getValue();
		int month = spinnerMonth.getMonth();
		int days = spinnerDay.getValue();
		int hours = spinnerHour.getValue();
		int minutes = spinnerMinutes.getValue();
		int seconds = spinnerSeconds.getValue();
		// Verification de la validite des valeurs entrees
		if(year <= 0 || month < 0 || month > 11 || days < 0 || days > 31 || hours < 0 || hours > 23 || minutes < 0 || minutes > 60 || seconds < 0 || seconds > 60) {
			return null;
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
           return sdf.parse(year + "-" + (month+1) + "-" + days + " " + hours + ":" + minutes + ":" + seconds);
        } catch (ParseException e) {
            return null;
        }
	}

	public void setDate(Date newDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
		String dateAsString = sdf.format(newDate);
		String[] elems = dateAsString.split(" ");
		assert(elems.length == 6);
		spinnerYear.setValue(Integer.parseInt(elems[0]));
		spinnerMonth.setMonth(Integer.parseInt(elems[1])-1);
		spinnerDay.setValue(Integer.parseInt(elems[2]));
		spinnerHour.setValue(Integer.parseInt(elems[3]));
		spinnerMinutes.setValue(Integer.parseInt(elems[4]));
		spinnerSeconds.setValue(Integer.parseInt(elems[5]));
	}
	
}
