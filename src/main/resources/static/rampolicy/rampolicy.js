Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var reload = function () {
    userStore.load();
  };

  var policyStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../aliyunram/listPoliciesForUser.htm',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['policyName', 'policyType', 'description', 'defaultVersion', 'attachDate']
  });

  var userStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../aliyunram/searchRAMUser.htm',
    rootFlag: 'data',
    pageSize: 200,
    fields: ['userId', 'userName', 'displayName', 'mobilePhone', 'email', 'comments', 'createDate', 'updateDate']
  });

  userStore.on('beforeload', function (store, options) {
    options.params = Ext.apply(options.params || {}, searchForm.getForm().getValues());
  });

  policyStore.on('beforeload', function (store, options) {
    var select = MyExt.util.SelectGridModel(userGrid, true);
    if (!select) {
      return;
    }
    options.params = Ext.apply(options.params || {}, {
      userName: select[0].data['userName']
    });
  });


  var policyGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    title: 'Policy列表',
    store: policyStore,
    hasInfoBbar: true,
    hasBbar: false,
    columns: [{
      header: "名称",
      dataIndex: 'policyName',
      width: 300
    }, {
      header: "类型",
      dataIndex: 'policyType',
      width: 80
    }, {
      header: "描述",
      dataIndex: 'description',
      flex: 1
    }, {
      header: "版本",
      dataIndex: 'defaultVersion',
      width: 50
    }, {
      header: "授权日期",
      dataIndex: 'attachDate',
      width: 150
    }],
    tbar: [
      {
        text: '合并',
        iconCls: 'MyExt-package',
        handler: function () {
          var select = MyExt.util.SelectGridModel(policyGrid, false);
          if (!select) {
            return;
          }

          var selectUser = MyExt.util.SelectGridModel(userGrid, true);
          if (!selectUser) {
            return;
          }

          if (select.length < 2) {
            MyExt.Msg.alert('至少选择两条Policy!');
            return;
          }

          let policyArray = new Array();
          let msg = "<hr>";
          for (let i = 0; i < select.length; i++) {
            policyArray[i] = new Object();
            policyArray[i].policyType = select[i].data["policyType"];
            policyArray[i].policyName = select[i].data["policyName"];
            policyArray[i].versionId = select[i].data["defaultVersion"];
            msg += select[i].data["policyName"] + "<br/>";
          }

          MyExt.util.MessageConfirm('是否合并以下策略:' + msg, function () {
            MyExt.util.Ajax('../aliyunram/mergePolicy.htm', {
              userName: selectUser[0].data["userName"],
              policyArray: Ext.JSON.encode(policyArray)
            }, function (data) {
              policyStore.load();
              MyExt.Msg.alert('合并成功!');
            });
          });
        }
      }, {
        text: '合并预览',
        iconCls: 'MyExt-check',
        handler: function () {
          var select = MyExt.util.SelectGridModel(policyGrid, false);
          if (!select) {
            return;
          }
          if (select.length < 2) {
            MyExt.Msg.alert('至少选择两条Policy!');
            return;
          }

          let policyArray = new Array();
          let msg = "<hr>";
          for (let i = 0; i < select.length; i++) {
            policyArray[i] = new Object();
            policyArray[i].policyType = select[i].data["policyType"];
            policyArray[i].policyName = select[i].data["policyName"];
            policyArray[i].versionId = select[i].data["defaultVersion"];
            msg += select[i].data["policyName"] + "<br/>";
          }
          MyExt.util.Ajax('../aliyunram/mergePolicyPreview.htm', {
            policyArray: Ext.JSON.encode(policyArray)
          }, function (data) {
            MyExt.Msg.alert('预览刷新!');
            policyDocument.setValue("合并完成后,Policy的数量: " + data.total + "\n------------------------------------------------\n" + MyExt.util.formatToJson(data.data));
          });
        }
      }, {
        text: '解绑',
        iconCls: 'MyExt-unbundling',
        handler: function () {
          var select = MyExt.util.SelectGridModel(policyGrid, false);
          if (!select) {
            return;
          }

          var selectUser = MyExt.util.SelectGridModel(userGrid, true);
          if (!selectUser) {
            return;
          }
          let policyArray = new Array();
          let msg = "<hr>";
          for (let i = 0; i < select.length; i++) {
            policyArray[i] = new Object();
            policyArray[i].policyType = select[i].data["policyType"];
            policyArray[i].policyName = select[i].data["policyName"];
            policyArray[i].versionId = select[i].data["defaultVersion"];
            msg += select[i].data["policyName"] + "<br/>";
          }

          MyExt.util.MessageConfirm('是否确定解绑下策略:' + msg, function () {
            MyExt.util.Ajax('../aliyunram/detachPolicyFromUser.htm', {
              userName: selectUser[0].data["userName"],
              policyArray: Ext.JSON.encode(policyArray)
            }, function (data) {
              MyExt.Msg.alert('解绑成功!');
              policyStore.load();
            });
          });

        }
      }, {
        text: '引用预览',
        iconCls: 'MyExt-check',
        handler: function () {
          var select = MyExt.util.SelectGridModel(policyGrid, false);
          if (!select) {
            return;
          }
          MyExt.util.Ajax('../aliyunram/detachPolicyPrevew.htm', {
            policyType: select[0].data["policyType"],
            policyName: select[0].data["policyName"],
            lineSeparate: '\n'
          }, function (data) {
            MyExt.Msg.alert('预览刷新!');
            policyDocument.setValue(data.data);
          });
        }
      }, {
        text: '解绑并删除',
        iconCls: 'MyExt-delete',
        handler: function () {
          var select = MyExt.util.SelectGridModel(policyGrid, true);
          if (!select) {
            return;
          }

          MyExt.Msg.alert('请稍等...');

          MyExt.util.Ajax('../aliyunram/detachPolicyPrevew.htm', {
            policyType: select[0].data["policyType"],
            policyName: select[0].data["policyName"],
            lineSeparate: '<br>'
          }, function (dataInfo) {
            MyExt.util.MessageConfirm('是否确定删除策略,同时解除以下授权:<br>' + dataInfo.data, function () {
              MyExt.util.Ajax('../aliyunram/deletePolicy.htm', {
                policyType: select[0].data["policyType"],
                policyName: select[0].data["policyName"],
                needDelete: 'true'
              }, function (data) {
                MyExt.Msg.alert('删除成功!');
                policyStore.load();
              });
            });
          });


        }
      }
    ],
    listeners: {
      itemclick: function (grid, record) {
        policyDocument.setValue("");
      },
      itemdblclick: function (grid, record) {
        var select = MyExt.util.SelectGridModel(grid, true);
        if (!select) {
          policyDocument.setValue("");
          return;
        }
        MyExt.util.Ajax('../aliyunram/getPolicy.htm', {
          policyType: select[0].data["policyType"],
          policyName: select[0].data["policyName"],
          versionId: select[0].data["defaultVersion"]
        }, function (data) {
          policyDocument.setValue("授权列表:\n" + data.data.preview + "\n--------------------------------------------------------------\n" + MyExt.util.formatToJson(data.data.policyDocument));
        });
      }
    }
  });

  var policyDocument = new Ext.form.field.TextArea({
    autoScroll: true,
    readOnly: true,
    margin: 2
  });

  var policyPanel = Ext.create('Ext.panel.Panel', {
    title: 'Console',
    layout: 'fit',
    region: 'south',
    split: true,
    height: 450,
    // html: '<p>World!</p>'
    items: [policyDocument]
  });


  var searchForm = Ext.create('Ext.form.Panel', {
    region: 'north',
    frame: false,
    margin: '0 0 5 0',
    height: 80,
    bodyStyle: {
      padding: '15px 0px 0px 10px',
      background: 'rgb(223,233,246)'
    },
    fieldDefaults: {
      labelWidth: 30
    },
    defaults: {
      width: 300
    },
    defaultType: 'textfield',
    buttonAlign: 'left',
    items: [{
      fieldLabel: '搜索',
      width: 400,
      emptyText: '名称',
      name: 'simpleSearch',
      enableKeyEvents: true,
      listeners: {
        keypress: function (thiz, e) {
          if (e.getKey() == Ext.EventObject.ENTER) {
            userGrid.getPageToolbar().moveFirst();
          }
        }
      }
    }]
  });


  var userGrid = Ext.create('MyExt.Component.GridPanel', {
    region: 'center',
    //split: true,
    // width: 500,
    title: '用户列表',
    store: userStore,
    columns: [{
      dataIndex: 'userId',
      header: 'userId',
      hidden: true
    }, {
      dataIndex: 'userName',
      header: "用户名",
      width: 150
    }, {
      dataIndex: 'displayName',
      header: "显示名",
      width: 150
    }, {
      dataIndex: 'comments',
      header: "备注",
      flex: 1,
    }, {
      header: "创建时间",
      dataIndex: 'createDate',
      width: 150
    }],
    listeners: {
      itemclick: function (grid, record) {
        policyDocument.setValue("");
        var select = MyExt.util.SelectGridModel(grid, true);
        if (!select) {
          policyStore.removeAll();
          return;
        }
        policyStore.load();
      },
      itemdblclick: function (grid, record) {

      }
    }
  });

  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [{
      layout: 'border',
      border: false,
      split: true,
      region: 'west',
      width: 600,
      items: [searchForm, userGrid]
    }, {
      layout: 'border',
      region: 'center',
      border: false,
      items: [policyGrid, policyPanel]
    }]
  });

  reload();

})