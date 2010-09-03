import wx, sys

class MainWindow(wx.Frame):

    def __init__(self, parent, title):
        wx.Frame.__init__(self, parent, title = title, size = (200, 100))
        self.control = wx.TextCtrl(self, style = wx.TE_MULTILINE)
        self.CreateStatusBar()

        filemenu = wx.Menu()

        aboutItem = filemenu.Append(wx.ID_ABOUT, "&About", " Information about this program")
        filemenu.AppendSeparator()
        exitItem = filemenu.Append(wx.ID_EXIT, "E&xit", " Terminate the program")

        ### Bind actions 
        self.Bind(wx.EVT_MENU, self.onExit, exitItem)
        self.Bind(wx.EVT_MENU, self.onAbout, aboutItem)

        menuBar = wx.MenuBar()
        menuBar.Append(filemenu, "&File")
        self.SetMenuBar(menuBar)
        self.Show(True)

    def onExit(self, event):
        print "Exiting..."
        sys.exit(0)

    def onAbout(self, event):
        dlg = wx.MessageDialog(self, "A small text editor", "About Sample Editor")
        dlg.ShowModal()
        dlg.Destroy()


app = wx.App(False)
frame = MainWindow(None, "Stample editor")
app.MainLoop()
