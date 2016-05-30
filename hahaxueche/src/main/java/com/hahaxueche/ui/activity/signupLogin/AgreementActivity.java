package com.hahaxueche.ui.activity.signupLogin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/5/30.
 */
public class AgreementActivity extends SLBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        initViews();
        loadDatas();
        initEvents();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvAgreement = Util.instence(this).$(this, R.id.tv_agreement);
    }

    private void loadDatas() {
        mTvAgreement.setText("您使用哈哈学车APP前，请您务必仔细阅读并透彻理解本声明。您可以选择不使用哈哈学车APP，但如果您使用哈哈学车APP，您的使用行为将被视为对本声明全部内容的认可。\n" +
                "\n\n1. 鉴于哈哈学车APP为武汉小松科技有限公司（以下简称我司）开发的一款手机APP，一个面向学员和教练的O2O学车平台；本身不对教练所上传的信息内容负责。\n" +
                "\n\n2. 哈哈学车APP本身不提供驾驶员培训服务，本公司与教练不存在雇佣关系，亦不对教练在哈哈学车APP上提供的服务作任何明示或者默认的担保。\n" +
                "\n\n3. 哈哈学车APP作为教练信息的展示平台，不对任何教练作任何推荐，学员明确同意对其在平台上做出的选择承担全部责任。\n" +
                "\n\n4. 学员明确同意其使用本APP所存在的风险将完全由自己承担；因其使用本APP而产生的一切后果也由其自己承担，哈哈学车APP不对学员承担任何责任，包括在教学过程中所产生的安全事故。\n" +
                "\n\n5. 哈哈学车对由于教练未能按照哈哈学车的服务标准进行履行、或依据其在哈哈学车APP中提供的信息进行交易引起的对任何第三方的损害不承担任何赔偿责任；教练应对前述造成的损害进行完全的赔偿。\n" +
                "\n\n6. 哈哈学车不声明或保证哈哈学车APP或可从哈哈学车APP下载的内容不带有计算机病毒或类似垃圾或破坏功能。\n" +
                "\n\n7. 哈哈学车不担保哈哈学车APP中提供的平台服务一定能满足学员或者教练的要求，亦不对服务提供者所发布信息的删除或者储存失败负责。\n" +
                "\n\n8. 若哈哈学车已经明示哈哈学车APP运营方式发生变更并提醒教练或学员应当注意事项，教练或学员未按要求操作所产生的一切后果由教练或者学员自行承担。\n" +
                "\n\n9. 教练和学员确认其知悉，在使用哈哈学车APP提供的平台服务中存在有来自任何其他人的包括威胁性的、诽谤性的、令人反感的或非法的内容或行为或对他人权利的侵犯（包括知识产权）的匿名或冒名的信息的风险，教练和学员需承担以上风险，对因此导致任何教练及学员不正当或非法使用服务产生的直接、间接、偶然、惩罚性的损害，不承担任何责任。\n" +
                "\n\n10. 因教练上传到哈哈学车APP中的内容违法或者侵犯第三方的合法权益而导致哈哈学车对第三方承担任何性质的索赔、补偿或罚款而遭受损失（直接的、间接的、偶然的、惩罚性的损失），教练对于哈哈学车遭受的上述损失承担完全的赔偿责任。");
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgreementActivity.this.finish();
            }
        });
    }
}
