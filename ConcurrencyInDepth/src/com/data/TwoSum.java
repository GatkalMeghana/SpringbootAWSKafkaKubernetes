package com.data;

public class TwoSum {

	public static int[] twoSum(int[] nums, int target) {
		int left = 0;
		int right = nums.length-1;
		while(left < right) {
			int sum = nums[left] + nums[right];
			if(sum==target) {
				System.out.println(left+"-----------"+right);
				return new int[] {nums[left], nums[right]};
			}
			if(sum < target) {
				left++;
			}else{
				right--;
			}
		}
		return new int[] {};
	}
	public static void main(String[] args) {
		int sum = 9;
		int[] nums = {2,7,11,15};
		int[] result = twoSum(nums, sum);
		System.out.println(result[0]+"  "+result[1]);
	}

}
