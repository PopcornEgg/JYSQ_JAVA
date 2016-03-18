using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace QQZeoneMarketing
{
    public partial class QQClientUI : UserControl
    {
        private delegate void LogDelegate(string msg); 

        public QQClientUI()
        {
            InitializeComponent();

            
            QQClient.Debug.OnLog = (string msg) =>
            {
                this.Invoke(new LogDelegate((string _msg) => {
                    logger.Items.Add(_msg);
                    logger.SelectedIndex = logger.Items.Count - 1;
                }), msg);
            };
        }

        void DoWork()
        {
            //QQClient.Test.FindQQ();
            QQClient.QQClient qqClient = new QQClient.QQClient();
            qqClient.Start();
        }


        private void btnStart_Click(object sender, EventArgs e)
        {
            btnStart.Enabled = false;
            logger.Items.Add("");
            logger.Items.Add("");

            Thread th = new Thread(new ThreadStart(DoWork));
            th.IsBackground = true;
            th.SetApartmentState(ApartmentState.STA);
            th.Start();
        }

        private void btnTestFindFriend_Click(object sender, EventArgs e)
        {
            btnTestFindFriend.Enabled = false;
            logger.Items.Add("");
            logger.Items.Add("");

            Thread th = new Thread(new ThreadStart(() => {
                QQClient.Test.TestFindUI();
            }));
            th.IsBackground = true;
            th.SetApartmentState(ApartmentState.STA);
            th.Start();
        }

        private void btnTestAddFriendState_Click(object sender, EventArgs e)
        {
            //btnTestAddFriendState.Enabled = false;
            logger.Items.Add("");
            logger.Items.Add("");

            Thread th = new Thread(new ThreadStart(() => {
                QQClient.Test.TestAddFriendStateUI();
            }));
            th.IsBackground = true;
            th.SetApartmentState(ApartmentState.STA);
            th.Start();
            
        }
		private void btnTestLogin_Click(object sender, EventArgs e)
        {
            btnTestFindFriend.Enabled = false;
            logger.Items.Add("");
            logger.Items.Add("");

            Thread th = new Thread(new ThreadStart(() =>
            {
                QQClient.Test.TestLogin();
            }));
            th.IsBackground = true;
            th.SetApartmentState(ApartmentState.STA);
            th.Start();
        }

        private void 打开所有QQ窗口ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            btnTestFindFriend.Enabled = false;
            logger.Items.Add("");
            logger.Items.Add("");

            Thread th = new Thread(new ThreadStart(() =>
            {
                QQClient.Tools.OpenAllQQWindow();
            }));
            th.IsBackground = true;
            th.SetApartmentState(ApartmentState.STA);
            th.Start();
        }
    }
}
