jest.setTimeout(50000);

const platformInfo = process.env.uniTestPlatformInfo.toLocaleLowerCase()
const isAndroid = platformInfo.startsWith('android')
const isIos = platformInfo.startsWith('ios')
const isHarmony = platformInfo.startsWith('harmony')
const isApp = isAndroid || isIos || isHarmony
const isWeb = platformInfo.startsWith('web')
const isMP = platformInfo.startsWith('mp')
const isAppWebView = process.env.UNI_AUTOMATOR_APP_WEBVIEW == 'true'

let pageIndex = 0
// 跑腿小程序业务页面
const pages = [
  '/pages/home/index',
  '/pages/order/list',
  '/pages/mine/mine',
  '/pages/login/login',
  '/pages/register/register',
  '/pages/address/address-list',
  '/pages/address/address-edit',
  '/pages/message/list',
  '/pages/mine/change-password',
  '/pages/mine/edit-profile',
  '/pages/order/create',
  '/pages/order/detail',
  '/pages/order/evaluate',
  '/pages/runner/active-orders',
  '/pages/runner/auth-apply',
  '/pages/runner/auth-status',
  '/pages/runner/earnings-details',
  '/pages/runner/earnings',
  '/pages/runner/pending-orders',
  '/pages/runner/withdrawal-records',
  '/pages/runner/withdrawal',
  '/pages/common/privacy',
  '/pages/common/webview',
]

// 跑腿小程序仅使用上述业务页面，已移除示例页面的条件 push

let page;
let windowInfo

function getWaitForTagName(pagePath) {
  // 跑腿小程序业务页面默认使用 view
  if (pagePath === '/pages/common/webview') {
    return 'web-view'
  }
  return 'view'
}

// 将页面数组分组
const BATCH_SIZE = 20;
const pageBatches = [];
for (let i = 0; i < pages.length; i += BATCH_SIZE) {
  pageBatches.push(pages.slice(i, i + BATCH_SIZE));
}

// 为每个批次创建独立的测试套件
pageBatches.forEach((batch, batchIndex) => {
  describe(`Page Screenshot Batch ${batchIndex + 1}`, () => {
    let localPageIndex = 0;
    
    beforeAll(async () => {
      console.log(`Starting batch ${batchIndex + 1} with ${batch.length} pages`);
      windowInfo = await program.callUniMethod('getWindowInfo');
    });
    
    afterAll(async () => {
      console.log(`Finished batch ${batchIndex + 1}`);
    });
    
    test.each(batch)("%s", async () => {
      const currentPagePath = batch[localPageIndex];
      page = await program.reLaunch(currentPagePath);
      await page.waitFor(getWaitForTagName(currentPagePath));
      console.log("Taking screenshot: ", pageIndex, currentPagePath);
      let fullPage = true;

      const screenshotParams = {
        fullPage
      }
      if (!fullPage && !isAppWebView) {
        screenshotParams.offsetY = isApp ? `${windowInfo.safeAreaInsets.top + 44}` : '0'
      }

      const image = await program.screenshot(screenshotParams);
      expect(image).toSaveImageSnapshot({
        customSnapshotIdentifier() {
          return `__pages_test__/${currentPagePath.replace(/\//g, "-").substring(1)}`
        }
      })
      await page.waitFor(800);
      localPageIndex++;
    });
  });
});
