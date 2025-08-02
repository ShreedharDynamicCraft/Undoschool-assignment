#!/bin/bash

# Course Search API - Demo Script
# This script demonstrates all the API features for the video recording

set -e

BASE_URL="http://localhost:8080/api"

echo "🎬 Course Search API - Feature Demo"
echo "==================================="
echo ""

# Function to make API call and format output
api_call() {
    local url="$1"
    local description="$2"
    
    echo "📡 $description"
    echo "🔗 URL: $url"
    echo "📄 Response:"
    curl -s "$url" | jq . || curl -s "$url"
    echo ""
    echo "---"
    echo ""
}

# Check if the API is running
echo "🏥 Health Check"
api_call "$BASE_URL/health" "Checking if API is running"

# Basic search
echo "🔍 Basic Text Search"
api_call "$BASE_URL/search?q=programming" "Searching for courses with 'programming'"

# Category filtering
echo "📚 Category Filtering"
api_call "$BASE_URL/search?category=Science" "Filtering by Science category"

# Age range filtering
echo "👶 Age Range Filtering"
api_call "$BASE_URL/search?minAge=10&maxAge=14" "Courses for ages 10-14"

# Price range filtering
echo "💰 Price Range Filtering"
api_call "$BASE_URL/search?minPrice=100&maxPrice=200" "Courses between $100-200"

# Course type filtering
echo "🎯 Course Type Filtering"
api_call "$BASE_URL/search?type=ONE_TIME" "One-time workshop courses"

# Date filtering
echo "📅 Date Filtering"
api_call "$BASE_URL/search?startDate=2025-09-01T00:00:00" "Courses starting from September 2025"

# Sorting options
echo "📊 Sorting - Price Ascending"
api_call "$BASE_URL/search?sort=priceAsc&size=5" "5 cheapest courses"

echo "📊 Sorting - Price Descending"
api_call "$BASE_URL/search?sort=priceDesc&size=5" "5 most expensive courses"

echo "📊 Sorting - Upcoming Sessions"
api_call "$BASE_URL/search?sort=upcoming&size=5" "5 earliest upcoming courses"

# Pagination
echo "📃 Pagination"
api_call "$BASE_URL/search?page=0&size=3" "First page with 3 results"
api_call "$BASE_URL/search?page=1&size=3" "Second page with 3 results"

# Complex query
echo "🔬 Complex Search Query"
api_call "$BASE_URL/search?q=art&category=Art&minPrice=50&maxPrice=150&sort=upcoming" "Art courses between $50-150, sorted by upcoming sessions"

# Fuzzy search examples
echo "🤖 Fuzzy Search (Typo Handling)"
api_call "$BASE_URL/search?q=programing" "Search with typo: 'programing' (missing 'm')"
api_call "$BASE_URL/search?q=mathmatics" "Search with typo: 'mathmatics' (extra 'm')"
api_call "$BASE_URL/search?q=dinasour" "Search with typo: 'dinasour' (should find dinosaur courses)"

# Autocomplete suggestions
echo "💡 Autocomplete Suggestions"
api_call "$BASE_URL/search/suggest?q=py" "Suggestions for 'py'"
api_call "$BASE_URL/search/suggest?q=art" "Suggestions for 'art'"
api_call "$BASE_URL/search/suggest?q=sci" "Suggestions for 'sci'"
api_call "$BASE_URL/search/suggest?q=phy" "Suggestions for 'phy'"

# Edge cases
echo "🎭 Edge Cases"
api_call "$BASE_URL/search?q=" "Empty search query"
api_call "$BASE_URL/search?minAge=20&maxAge=25" "Age range with no results"
api_call "$BASE_URL/search?minPrice=1000" "Very high price filter"

echo "🎉 Demo Complete!"
echo ""
echo "📝 Summary of demonstrated features:"
echo "✅ Full-text search with fuzzy matching"
echo "✅ Category, age, price, type, and date filtering"
echo "✅ Multiple sorting options"
echo "✅ Pagination support"
echo "✅ Autocomplete suggestions"
echo "✅ Complex query combinations"
echo "✅ Typo tolerance in search"
echo "✅ Edge case handling"
echo ""
echo "🎬 This demo covers all requirements for Assignment A and Assignment B!"
