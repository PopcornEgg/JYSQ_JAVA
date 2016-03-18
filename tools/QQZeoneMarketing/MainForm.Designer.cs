namespace QQZeoneMarketing
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.button2 = new System.Windows.Forms.Button();
            this.button1 = new System.Windows.Forms.Button();
            this.flowLayoutPanel1 = new System.Windows.Forms.FlowLayoutPanel();
            this.btn_Sprder = new System.Windows.Forms.Button();
            this.btn_LiuHen = new System.Windows.Forms.Button();
            this.btn_QQClient = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.button3 = new System.Windows.Forms.Button();
            this.button4 = new System.Windows.Forms.Button();
            this.flowLayoutPanel1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(3, 109);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(112, 100);
            this.button2.TabIndex = 1;
            this.button2.Text = "说话";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(121, 109);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(103, 100);
            this.button1.TabIndex = 0;
            this.button1.Text = "测试点赞";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // flowLayoutPanel1
            // 
            this.flowLayoutPanel1.Controls.Add(this.btn_Sprder);
            this.flowLayoutPanel1.Controls.Add(this.btn_LiuHen);
            this.flowLayoutPanel1.Controls.Add(this.button2);
            this.flowLayoutPanel1.Controls.Add(this.button1);
            this.flowLayoutPanel1.Controls.Add(this.btn_QQClient);
            this.flowLayoutPanel1.Controls.Add(this.button3);
            this.flowLayoutPanel1.Controls.Add(this.button4);
            this.flowLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.flowLayoutPanel1.Location = new System.Drawing.Point(0, 0);
            this.flowLayoutPanel1.Name = "flowLayoutPanel1";
            this.flowLayoutPanel1.Size = new System.Drawing.Size(453, 308);
            this.flowLayoutPanel1.TabIndex = 1;
            this.flowLayoutPanel1.Paint += new System.Windows.Forms.PaintEventHandler(this.flowLayoutPanel1_Paint);
            // 
            // btn_Sprder
            // 
            this.btn_Sprder.Font = new System.Drawing.Font("微软雅黑", 42F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btn_Sprder.ForeColor = System.Drawing.Color.Red;
            this.btn_Sprder.Location = new System.Drawing.Point(3, 3);
            this.btn_Sprder.Name = "btn_Sprder";
            this.btn_Sprder.Size = new System.Drawing.Size(200, 100);
            this.btn_Sprder.TabIndex = 0;
            this.btn_Sprder.Text = "爬虫";
            this.btn_Sprder.UseVisualStyleBackColor = true;
            this.btn_Sprder.Click += new System.EventHandler(this.btn_Sprder_Click);
            // 
            // btn_LiuHen
            // 
            this.btn_LiuHen.Font = new System.Drawing.Font("微软雅黑", 48F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btn_LiuHen.ForeColor = System.Drawing.Color.OrangeRed;
            this.btn_LiuHen.Location = new System.Drawing.Point(209, 3);
            this.btn_LiuHen.Name = "btn_LiuHen";
            this.btn_LiuHen.Size = new System.Drawing.Size(200, 100);
            this.btn_LiuHen.TabIndex = 2;
            this.btn_LiuHen.Text = "留痕";
            this.btn_LiuHen.UseVisualStyleBackColor = true;
            // 
            // btn_QQClient
            // 
            this.btn_QQClient.Location = new System.Drawing.Point(230, 109);
            this.btn_QQClient.Name = "btn_QQClient";
            this.btn_QQClient.Size = new System.Drawing.Size(103, 100);
            this.btn_QQClient.TabIndex = 3;
            this.btn_QQClient.Text = "QQ客户端";
            this.btn_QQClient.UseVisualStyleBackColor = true;
            this.btn_QQClient.Click += new System.EventHandler(this.btn_QQClient_Click);
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.flowLayoutPanel1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(453, 308);
            this.panel1.TabIndex = 2;
            // 
            // button3
            // 
            this.button3.Location = new System.Drawing.Point(339, 109);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(102, 100);
            this.button3.TabIndex = 4;
            this.button3.Text = "（测试）获取N条QQ信息，并锁定";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // button4
            // 
            this.button4.Location = new System.Drawing.Point(3, 215);
            this.button4.Name = "button4";
            this.button4.Size = new System.Drawing.Size(112, 90);
            this.button4.TabIndex = 5;
            this.button4.Text = "（测试）修改一条QQ的锁定信息";
            this.button4.UseVisualStyleBackColor = true;
            this.button4.Click += new System.EventHandler(this.button4_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(453, 308);
            this.Controls.Add(this.panel1);
            this.Name = "MainForm";
            this.Text = "MainForm";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.flowLayoutPanel1.ResumeLayout(false);
            this.panel1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.FlowLayoutPanel flowLayoutPanel1;
        private System.Windows.Forms.Button btn_Sprder;
        private System.Windows.Forms.Button btn_LiuHen;
        private System.Windows.Forms.Button btn_QQClient;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.Button button4;

    }
}