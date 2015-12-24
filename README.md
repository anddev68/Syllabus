しゅらばす
===
岐阜高専専用のシラバス閲覧ツールです。  
GooglePlayで公開中。


カスタマイズ方法
---
### task/SubjectsDownloader ###
教科一覧のダウンローダです。学校の仕様に合わせて変更してください。
このクラスはサーバへ接続し、教科の一覧を取得するものです。
基本的にはこのクラスを変更することで動くはずです。
<dl>
  <dt>SubjectsDownloader#LoadingBackground() : ArrayList<Subject></dt>
  <dd>
    Subjectオブジェクト(教科)のリストを返すようにしてください。
  </dd>
</dl>

### record/Subject ###
教科を表すクラス。
学年ID、学科IDはそれぞれSubjectsDownloaderと統一すること。

### record/MyClass ###
新たに実装した受講クラス。
選択科目を除きたい、あるいは諸事情により下の学年のシラバスを見たいといった要望に応えました。
現在ユーザ切り替えは未実装なので0を入れておいてください。




### データベース設計 ###
Railsを意識してあります。
そのため、recordパッケージと対になっています。




更新履歴
---
<dl>
  <dt>2015/11/29</dt>
  <dd>マテリアルデザインを実装。</dd>
</dl>




ライセンス
---
This software is released under the Apache2.0 License, see LICENSE.txt.


