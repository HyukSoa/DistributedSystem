import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by huanyuhello on 5/9/2016.
 */

public class GUI extends JFrame {

    GameMsg msg = new GameMsg();
    JList jListKnow02;
    JScrollPane jScrollPane02;
    JSplitPane jSplitPane;
    MyPanel jPanelTop;
    JPanel jPanelBottom;
    DefaultListModel defaultListModel;
    int[][] maze = new int[20][20];
    ArrayList list = new ArrayList();
    GameMsg gameMsg;
    private static final long serialVersionUID = 1L;

    class MyPanel extends JPanel {
        /***/
        private static final long serialVersionUID = 1L;


        public void paint(Graphics graphics) {

            super.paint(graphics);

            graphics.setColor(Color.BLACK);
            //设置画笔大小
            graphics.setFont(new Font(null, 0, 45));

            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    //g.setColor(Color.blue);
                    graphics.setFont(new Font(null, 0, 45));
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(50 + j * 30, 50 + i * 30, 30, 30);
                    if (maze[i][j] == -1) {
                        graphics.setColor(Color.BLUE);
                        graphics.setFont(new Font(null, 0, 30));
                        graphics.drawString("*", 58 + j * 30, 83 + i * 30);
                    }
                    if ((maze[i][j] != 0)&&(maze[i][j] != -1)) {
                        graphics.setColor(Color.RED);
                        graphics.setFont(new Font(null, 0, 25));
                        graphics.drawString(String.valueOf(maze[i][j]), 58 + j * 30, 75 + i * 30);
                    }
                }
            }
            //System.out.println("GUI list:  " + gameMsg.mazeState.GetPlayerScoreList().clone());
            list = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
            String string = new String();
            defaultListModel.clear();
            for (int i = 0; i < list.size(); i++) {

                string = "UserID : ";
                string = string.concat(list.get(i).toString());
                string = string.concat("  Curret Score : ");

                i++;
                string = string.concat(list.get(i).toString());
                string = string.concat("  Player ID : ");

                i++;
                string = string.concat(list.get(i).toString());

                defaultListModel.addElement(string);
            }

        }
    }

    public GUI(ArrayList listt, int[][] Mexturet) {

        list = (ArrayList) listt.clone();
        maze = Mexturet.clone();
        this.setBounds(300, 100, 1200, 1200);
        this.setTitle("JList的两种使用方式：推荐第二种使用方式");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jPanelTop = new MyPanel();
        jPanelBottom = new JPanel();
        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jPanelTop, jPanelBottom);
        //JSplitPane.setDividerLocation(225);
        jSplitPane.setDividerLocation(700);
        this.add(jSplitPane);

        //实例化模型
        defaultListModel = new DefaultListModel();
        //向模型中添加元素



        //根据模型实例化出来JList
        jListKnow02 = new JList(defaultListModel);
        jListKnow02.setBounds(20, 20, 500, 500);
        jScrollPane02 = new JScrollPane(jListKnow02);
        jPanelBottom.add(jListKnow02);
    }
}


