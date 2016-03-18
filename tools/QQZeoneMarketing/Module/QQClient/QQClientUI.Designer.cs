namespace QQZeoneMarketing
{
    partial class QQClientUI
    {
        /// <summary> 
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region 组件设计器生成的代码

        /// <summary> 
        /// 设计器支持所需的方法 - 不要
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.logger = new System.Windows.Forms.ListBox();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.btnStart = new System.Windows.Forms.ToolStripMenuItem();
            this.btnTestLogin = new System.Windows.Forms.ToolStripMenuItem();
            this.btnTestFindFriend = new System.Windows.Forms.ToolStripMenuItem();
            this.btnTestAddFriendState = new System.Windows.Forms.ToolStripMenuItem();
            this.测试放这儿ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.打开所有QQ窗口ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.menuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // logger
            // 
            this.logger.BackColor = System.Drawing.SystemColors.WindowFrame;
            this.logger.Dock = System.Windows.Forms.DockStyle.Fill;
            this.logger.ForeColor = System.Drawing.Color.White;
            this.logger.FormattingEnabled = true;
            this.logger.ItemHeight = 12;
            this.logger.Location = new System.Drawing.Point(0, 0);
            this.logger.Name = "logger";
            this.logger.Size = new System.Drawing.Size(688, 544);
            this.logger.TabIndex = 0;
            // 
            // menuStrip1
            // 
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnStart,
            this.btnTestLogin,
            this.btnTestFindFriend,
            this.btnTestAddFriendState,
            this.测试放这儿ToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(688, 25);
            this.menuStrip1.TabIndex = 2;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // btnStart
            // 
            this.btnStart.Name = "btnStart";
            this.btnStart.Size = new System.Drawing.Size(44, 21);
            this.btnStart.Text = "开始";
            this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
            // 
            // btnTestLogin
            // 
            this.btnTestLogin.Name = "btnTestLogin";
            this.btnTestLogin.Size = new System.Drawing.Size(68, 21);
            this.btnTestLogin.Text = "测试登录";
            this.btnTestLogin.Click += new System.EventHandler(this.btnTestLogin_Click);
            // 
            // btnTestFindFriend
            // 
            this.btnTestFindFriend.Name = "btnTestFindFriend";
            this.btnTestFindFriend.Size = new System.Drawing.Size(80, 21);
            this.btnTestFindFriend.Text = "测试查好友";
            this.btnTestFindFriend.Click += new System.EventHandler(this.btnTestFindFriend_Click);
            // 
            // btnTestAddFriendState
            // 
            this.btnTestAddFriendState.Name = "btnTestAddFriendState";
            this.btnTestAddFriendState.Size = new System.Drawing.Size(116, 21);
            this.btnTestAddFriendState.Text = "测试更新加友状态";
            this.btnTestAddFriendState.Click += new System.EventHandler(this.btnTestAddFriendState_Click);
            // 
            // 测试放这儿ToolStripMenuItem
            // 
            this.测试放这儿ToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.打开所有QQ窗口ToolStripMenuItem});
            this.测试放这儿ToolStripMenuItem.Name = "测试放这儿ToolStripMenuItem";
            this.测试放这儿ToolStripMenuItem.Size = new System.Drawing.Size(80, 21);
            this.测试放这儿ToolStripMenuItem.Text = "测试放这儿";
            // 
            // 打开所有QQ窗口ToolStripMenuItem
            // 
            this.打开所有QQ窗口ToolStripMenuItem.Name = "打开所有QQ窗口ToolStripMenuItem";
            this.打开所有QQ窗口ToolStripMenuItem.Size = new System.Drawing.Size(168, 22);
            this.打开所有QQ窗口ToolStripMenuItem.Text = "打开所有QQ窗口";
            this.打开所有QQ窗口ToolStripMenuItem.Click += new System.EventHandler(this.打开所有QQ窗口ToolStripMenuItem_Click);
            // 
            // QQClientUI
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.menuStrip1);
            this.Controls.Add(this.logger);
            this.Name = "QQClientUI";
            this.Size = new System.Drawing.Size(688, 545);
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ListBox logger;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem btnStart;
        private System.Windows.Forms.ToolStripMenuItem btnTestLogin;
        private System.Windows.Forms.ToolStripMenuItem btnTestFindFriend;
		private System.Windows.Forms.ToolStripMenuItem btnTestAddFriendState;
        private System.Windows.Forms.ToolStripMenuItem 测试放这儿ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 打开所有QQ窗口ToolStripMenuItem;
    }
}
