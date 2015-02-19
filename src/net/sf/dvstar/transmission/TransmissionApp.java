/*
 * transmission-remote-java remote control for transmission daemon
 *
 * Copyright (C) 2009-2011 Dmytro Starzhynskyi (dvstar)
 * http://transmission-rj.sourceforge.net/
 * http://code.google.com/p/transmission-remote-java/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.sf.dvstar.transmission;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.openide.util.Exceptions;

/**
 * The main class of the application.
 */
public class TransmissionApp extends SingleFrameApplication {

     @Override protected void initialize(java.lang.String[] args) {
        super.initialize(args);
     }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {

/*
        java.awt.EventQueue.invokeLater( new Runnable() {
            @Override
            public void run() {

        System.out.println("[TransmissionView]Setting L&F--------------------");
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
            //SwingUtilities.updateComponentTreeUI(app.getMainFrame());
        } catch (Exception e) {
            System.out.println("[TransmissionView]L&F failed to initialize");
        }
        System.out.println("[TransmissionView]Setting L&F--------------------");

            }
        });
*/

        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

/*
        final TransmissionView transmissionView = new TransmissionView(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                show(transmissionView);}
        });
*/
        //try {
            show(new TransmissionView(this));
        //} catch(org.jvnet.substance.api.UiThreadingViolationException ex) {}


    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of TransmissionApp
     */
    public static TransmissionApp getApplication() {
        return Application.getInstance(TransmissionApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(TransmissionApp.class, args);
    }
}
