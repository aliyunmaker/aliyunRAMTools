Ext.onReady(function () {
  Ext.tip.QuickTipManager.init();

  var reload = function () {
    policyStore.load();
    policyDocument.setValue("");
  };

  var policyStore = Ext.create('MyExt.Component.SimpleJsonStore', {
    dataUrl: '../aliyunram/searchPolicies.htm',
    rootFlag: 'data',
    pageSize: 200,
    sorters: [{
      property: 'createDate',
      direction: 'DESC'
    }],
    fields: ['policyName', 'policyType', 'description', 'defaultVersion', 'createDate', 'updateDate', 'attachmentCount']
  });

  policyStore.on('beforeload', function (store, options) {
    options.params = Ext.apply(options.params || {}, searchForm.getForm().getValues());
  });



  var policyFormWindow = new MyExt.Component.FormWindow({
    title: '操作',
    width: 500,
    height: 400,
    formItems: [{
      fieldLabel: 'Policy名称(*)',
      name: 'policyName',
      allowBlank: false
    }, {
      fieldLabel: 'Policy脚本(*)',
      name: 'policyDocument',
      xtype: 'textarea',
      emptyText: '',
      height: 300,
      allowBlank: false
    }],
    submitBtnFn: function () {
      var form = policyFormWindow.getFormPanel().getForm();
      if (form.isValid()) {
        MyExt.util.Ajax(policyFormWindow.getFormPanel().url, {
          formString: Ext.JSON.encode(form.getValues())
        }, function (data) {
          policyFormWindow.hide();
          policyStore.load();
          MyExt.Msg.alert('操作成功!');
        });
      }
    }
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
      header: "引用",
      dataIndex: 'attachmentCount',
      width: 50
    }, {
      header: "创建时间",
      dataIndex: 'createDate',
      width: 150
    }],
    tbar: [{
      text: '增加',
      iconCls: 'MyExt-add',
      handler: function () {
        policyFormWindow.changeFormUrlAndShow('../aliyunram/createPolicy.htm');
      }
    }, {
      text: '解绑所有授权',
      iconCls: 'MyExt-unbundling',
      handler: function () {
        var select = MyExt.util.SelectGridModel(policyGrid, true);
        if (!select) {
          return;
        }

        MyExt.util.Ajax('../aliyunram/detachPolicyPrevew.htm', {
          policyType: select[0].data["policyType"],
          policyName: select[0].data["policyName"],
          lineSeparate: '<br>'
        }, function (dataInfo) {
          MyExt.util.MessageConfirm('是否确定解绑以下授权:<br>' + dataInfo.data, function () {
            MyExt.util.Ajax('../aliyunram/deletePolicy.htm', {
              policyType: select[0].data["policyType"],
              policyName: select[0].data["policyName"],
              needDelete: 'false'
            }, function (data) {
              MyExt.Msg.alert('解绑成功!');
              reload();
            });
          });
        });
      }
    }, {
      text: '删除',
      iconCls: 'MyExt-delete',
      handler: function () {
        var select = MyExt.util.SelectGridModel(policyGrid, true);
        if (!select) {
          return;
        }

        MyExt.util.MessageConfirm('是否确定删除策略', function () {
          MyExt.util.Ajax('../aliyunram/deletePolicy.htm', {
            policyType: select[0].data["policyType"],
            policyName: select[0].data["policyName"],
            needDelete: 'true'
          }, function (data) {
            MyExt.Msg.alert('删除成功!');
            reload();
          });
        });


      }
    }],
    listeners: {
      itemclick: function (grid, record) {
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
      },
      itemdblclick: function (grid, record) {
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
    region: 'center',
    // split: true,
    // height: 450,
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
            policyGrid.getPageToolbar().moveFirst();
          }
        }
      }
    }]
  });


  Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [{
      layout: 'border',
      border: false,
      split: true,
      region: 'west',
      width: 800,
      items: [searchForm, policyGrid]
    }, policyPanel]
  });

  reload();

})