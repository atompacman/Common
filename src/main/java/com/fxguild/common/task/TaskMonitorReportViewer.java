package com.fxguild.common.task;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.fxguild.common.gui.CenteredJFrame;
import com.fxguild.common.gui.GUIUtils;

/**
 * Utility class that creates a window showing the information gathered by a {@link TaskMonitor} 
 * during its execution.
 */
public final class TaskMonitorReportViewer extends CenteredJFrame {

    //
    //  ~  CONSTANTS  ~  //
    //
    
    private static final Dimension               WIN_DIM         = new Dimension(800, 1000);
    private static final int                     DETAILS_PANEL_H = 300;
    private static final TaskMonitorReportViewer INSTANCE        = new TaskMonitorReportViewer();
    
    private static final long serialVersionUID = -1062507629632017771L;


    //
    //  ~  FIELDS  ~  //
    //
    
    private JScrollPane treePanel;
    private JPanel      detailsPanel;
    
    
    //
    //  ~  INIT  ~  //
    //
    
    public static void showReportWindow(TaskMonitor monitor) {
        INSTANCE.setMonitor(monitor);
    }
    
    private TaskMonitorReportViewer() {
        super(WIN_DIM);
        
        this.treePanel = new JScrollPane();
        this.detailsPanel = new JPanel();
        
        treePanel.setSize(new Dimension(WIN_DIM.width - DETAILS_PANEL_H, WIN_DIM.height));
        detailsPanel.setSize(new Dimension(DETAILS_PANEL_H, WIN_DIM.height));
        
        Container mainPane = getContentPane();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.add(treePanel);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUIUtils.setSystemLookAndFeel();
    }


    //
    //  ~  SET MONITOR  ~  //
    //
    
    public void setMonitor(TaskMonitor monitor) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(monitor.getTaskName());
        
        addChildNodes(monitor, root);
        
        JTree tree = new JTree(root);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.expandRow(i);
        }
        
        treePanel.removeAll();
        //treePanel.add(tree);
        //TODO REMOVE
        getContentPane().removeAll();
        getContentPane().add(new JScrollPane(tree));
        repaint();
        setVisible(true);
    }

    private void addChildNodes(TaskMonitor monitor, DefaultMutableTreeNode node) {
        for (TaskMonitor submonitor : monitor.getSubtasks()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(submonitor.getTaskName());
            for (Observation ob : submonitor.getObservations()) {
                childNode.add(new DefaultMutableTreeNode(ob.getMessage()));
            }
            addChildNodes(submonitor, childNode);
            node.add(childNode);
        }
    }
}
