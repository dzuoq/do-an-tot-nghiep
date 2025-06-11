import { IContactItems } from "../../../types/types";

// contact items text
const contactDirectlyText = (
  <>
    contact@dttech.vn
    <br />
    0396824864
  </>
);

const headQuaterText = (
  <>Số Nhà 20, Ngách 22 Ngõ 30, Đình Quán, Phúc Diễn, Bắc Từ Liêm, Hà Nội</>
);

const workWithUsText = (
  <>
    Gửi CV của bạn đến email:
    <br />
    duongdinhvan06@gmail.com
  </>
);

const customerServiceText = (
  <>
    duongdinhvan06@gmail.com
    <br />
    0396824864
  </>
);

const mediaRelationsText = (
  <>
    duongdinhvan06@gmail.com
    <br />
    0396824864
  </>
);

const vendorSupportText = (
  <>
    duongdinhvan06@gmail.com
    <br />
    0396824864
  </>
);

// contact items data
export const ContactItemsData: IContactItems[] = [
  {
    id: 1,
    title: "Liên hệ trực tiếp",
    content: contactDirectlyText,
  },
  {
    id: 2,
    title: "Trụ sở chính",
    content: headQuaterText,
  },
  {
    id: 3,
    title: "Cộng tác với chúng tôi",
    content: workWithUsText,
  },
  {
    id: 4,
    title: "Dịch vụ khách hàng",
    content: customerServiceText,
  },
  {
    id: 5,
    title: "Quan hệ truyền thông",
    content: mediaRelationsText,
  },
  {
    id: 6,
    title: "Hỗ trợ đối tác",
    content: vendorSupportText,
  },
];
