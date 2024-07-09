class Median {
    public static void main(String[] args) {
        class Solution {
            public double findMedianSortedArrays(int[] nums1, int[] nums2) {
                int n1 = nums1.length, n2 = nums2.length;
                int[] merged = new int[n1 + n2];
                int i = 0, j = 0, k = 0;

                while (i < n1 && j < n2) {
                    merged[k++] = (nums1[i] <= nums2[j]) ? nums1[i++] : nums2[j++];
                }
                while (i < n1) {
                    merged[k++] = nums1[i++];
                }
                while (j < n2) {
                    merged[k++] = nums2[j++];
                }

                int mid = merged.length / 2;
                return (merged.length % 2 == 0) ? (merged[mid - 1] + merged[mid]) / 2.0 : merged[mid];
            }
        }
    }
}