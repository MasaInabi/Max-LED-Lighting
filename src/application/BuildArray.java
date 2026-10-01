package application;

// كلاس بدل ارايليست
// لتخزين الارقام الي منقراها من انبوت وفايل
// عملت اراي  ونخزنت العناصر فيها ولما تفلل بنادي ميثود جرو بكبرها وبوسعها 
public class BuildArray {

    private int[] arr; // اراي لتخزين الارقام
    private int count; // كم رقم خزنا

    // لانشاء اراي لو الحجم اقل من 1 بخليه 1
    public BuildArray(int cap) {
        if (cap < 1) {
            cap = 1;
        }

        arr = new int[cap];
        count = 0;
    }
// تضيف رقم جديد لو الاراي فللت تكبرها
    public void put(int num) {
        if (count == arr.length) {
            grow(arr.length * 2);
        }

        arr[count] = num;
        count++;
    }

    // ترجع الرقم الموجود باندكس معينولو الاندكس غلط تعطي ايرور
    public int at(int index) {
        if (index < 0 || index >= count) {
            throw new RuntimeException("Invalid index");
        }

        return arr[index];
    }

    // ترجع عدد الارقام الموجودة 
    public int len() {
        return count;
    }
// تكبر الاراي (تعمل اراي جديدة اكبر وتنسخ القيم القديمة عليها)
    private void grow(int newSize) {
        int[] temp = new int[newSize];

        for (int i = 0; i < count; i++) {
            temp[i] = arr[i];
        }

        arr = temp;
    }
}