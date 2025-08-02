# Course Search API with Elasticsearch

A comprehensive Spring Boot application that provides a powerful search API for educational courses using Elasticsearch. This project implements advanced search features including filters, pagination, sorting, autocomplete suggestions, and fuzzy search capabilities.

## 🚀 Features

### Assignment A (Core Features)
- **Full-text search** on course titles and descriptions
- **Advanced filtering** by age range, category, course type, price range, and session dates
- **Flexible sorting** options (upcoming sessions, price ascending/descending)
- **Pagination support** for efficient data retrieval
- **RESTful API** with comprehensive endpoint documentation

### Assignment B (Bonus Features)
- **Autocomplete suggestions** for course titles
- **Fuzzy search** to handle typos and similar terms
- **Completion suggester** integration

## 🛠 Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data Elasticsearch**
- **Elasticsearch 8.11.0**
- **Docker & Docker Compose**
- **Maven** for dependency management
- **Lombok** for reduced boilerplate code
- **Testcontainers** for integration testing

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/ShreedharDynamicCraft/Undoschool-assignment.git
cd Undoschool-assignment
```

### 2. Start Elasticsearch

```bash
# Start Elasticsearch using Docker Compose
docker-compose up -d

# Verify Elasticsearch is running
curl http://localhost:9200
```

You should see a response like:
```json
{
  "name" : "course-search-node",
  "cluster_name" : "course-search-cluster",
  "cluster_uuid" : "...",
  "version" : {
    "number" : "8.11.0",
    ...
  }
}
```

### 3. Build and Run the Application

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Verify Sample Data Loading

Check the application logs to confirm that sample data has been loaded:

```
INFO - Loading sample course data...
INFO - Loaded 55 courses into Elasticsearch
INFO - Sample course data loaded successfully. Total courses: 55
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### 1. Search Courses

**Endpoint:** `GET /api/search`

**Description:** Search courses with multiple filters, pagination, and sorting options.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `q` | String | No | Search keyword for title/description | `q=python` |
| `minAge` | Integer | No | Minimum age filter | `minAge=10` |
| `maxAge` | Integer | No | Maximum age filter | `maxAge=16` |
| `category` | String | No | Course category filter | `category=Technology` |
| `type` | String | No | Course type (ONE_TIME, COURSE, CLUB) | `type=COURSE` |
| `minPrice` | Decimal | No | Minimum price filter | `minPrice=50.00` |
| `maxPrice` | Decimal | No | Maximum price filter | `maxPrice=200.00` |
| `startDate` | DateTime | No | Filter courses after this date (ISO format) | `startDate=2025-08-15T00:00:00` |
| `sort` | String | No | Sort option (upcoming, priceAsc, priceDesc) | `sort=priceAsc` |
| `page` | Integer | No | Page number (0-based) | `page=0` |
| `size` | Integer | No | Page size | `size=10` |

**Example Requests:**

```bash
# Basic search
curl "http://localhost:8080/api/search?q=programming"

# Search with filters
curl "http://localhost:8080/api/search?category=Technology&minAge=12&maxAge=18&sort=priceAsc"

# Search with price range and pagination
curl "http://localhost:8080/api/search?minPrice=100&maxPrice=300&page=0&size=5"

# Search for upcoming courses
curl "http://localhost:8080/api/search?startDate=2025-08-15T00:00:00&sort=upcoming"

# Fuzzy search (handles typos)
curl "http://localhost:8080/api/search?q=dinors" # Will match "Dinosaurs"
```

**Response Format:**
```json
{
  "total": 15,
  "page": 0,
  "size": 10,
  "courses": [
    {
      "id": "5",
      "title": "Python Programming for Beginners",
      "description": "Start your coding journey with Python...",
      "category": "Technology",
      "type": "COURSE",
      "gradeRange": "6th-12th",
      "minAge": 11,
      "maxAge": 18,
      "price": 250.00,
      "nextSessionDate": "2025-08-18T11:00:00"
    }
  ]
}
```

### 2. Autocomplete Suggestions

**Endpoint:** `GET /api/search/suggest`

**Description:** Get autocomplete suggestions for course titles.

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `q` | String | Yes | Partial title to get suggestions for | `q=py` |

**Example Requests:**

```bash
# Get suggestions for "py"
curl "http://localhost:8080/api/search/suggest?q=py"

# Get suggestions for "art"
curl "http://localhost:8080/api/search/suggest?q=art"

# Get suggestions for "sci"
curl "http://localhost:8080/api/search/suggest?q=sci"
```

**Response Format:**
```json
[
  "Python Programming for Beginners",
  "Physics Concepts"
]
```

### 3. Health Check

**Endpoint:** `GET /api/health`

**Description:** Check if the API is running.

```bash
curl "http://localhost:8080/api/health"
```

**Response:**
```
Course Search API is running!
```

## 🔍 Search Examples and Expected Results

### 1. Basic Text Search

```bash
curl "http://localhost:8080/api/search?q=math"
```
**Expected:** Returns courses with "math", "mathematics", etc. in title or description.

### 2. Category Filtering

```bash
curl "http://localhost:8080/api/search?category=Science"
```
**Expected:** Returns all science-related courses.

### 3. Age Range Filtering

```bash
curl "http://localhost:8080/api/search?minAge=8&maxAge=12"
```
**Expected:** Returns courses suitable for ages 8-12.

### 4. Price Sorting

```bash
curl "http://localhost:8080/api/search?sort=priceAsc&size=5"
```
**Expected:** Returns 5 cheapest courses.

### 5. Course Type Filtering

```bash
curl "http://localhost:8080/api/search?type=ONE_TIME"
```
**Expected:** Returns only one-time workshop courses.

### 6. Date Filtering

```bash
curl "http://localhost:8080/api/search?startDate=2025-09-01T00:00:00"
```
**Expected:** Returns courses starting from September 1st, 2025.

### 7. Complex Query

```bash
curl "http://localhost:8080/api/search?q=programming&category=Technology&minPrice=200&maxPrice=400&sort=upcoming&page=0&size=3"
```
**Expected:** Returns technology courses with "programming" in title/description, priced between $200-400, sorted by upcoming sessions.

### 8. Fuzzy Search Examples

```bash
# Typo handling
curl "http://localhost:8080/api/search?q=programing"  # Missing 'm'
curl "http://localhost:8080/api/search?q=mathmatics"  # Extra 'm'
curl "http://localhost:8080/api/search?q=dinasour"    # Misspelled "dinosaur"
```
**Expected:** These queries with typos should still return relevant results.

### 9. Autocomplete Examples

```bash
curl "http://localhost:8080/api/search/suggest?q=py"
# Expected: ["Python Programming for Beginners"]

curl "http://localhost:8080/api/search/suggest?q=art"
# Expected: ["Creative Art Workshop", "Art Workshop", ...]

curl "http://localhost:8080/api/search/suggest?q=phy"
# Expected: ["Advanced Physics Concepts", "Philosophy for Young Minds"]
```

## 🧪 Running Tests

### Unit and Integration Tests

```bash
# Run all tests
mvn test

# Run only integration tests
mvn test -Dtest="*IntegrationTest"

# Run tests with coverage
mvn test jacoco:report
```

The integration tests use Testcontainers to spin up a real Elasticsearch instance for testing.

## 🔧 Configuration

### Application Properties

Key configuration in `src/main/resources/application.properties`:

```properties
# Elasticsearch configuration
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=60s

# Server configuration
server.port=8080

# Logging
logging.level.com.undoschool.coursesearch=DEBUG
```

### Docker Compose Configuration

The `docker-compose.yml` file configures:
- Single-node Elasticsearch cluster
- No security (for development)
- Memory settings optimized for development
- Health checks

## 📊 Sample Data

The application includes 55 sample courses with diverse:
- **Categories:** Math, Science, Art, Technology, Sports, Music, Language Arts, etc.
- **Types:** COURSE, ONE_TIME, CLUB
- **Age Ranges:** From kindergarten to 12th grade
- **Price Points:** $60 to $350
- **Session Dates:** Spanning August through October 2025

## 🐛 Troubleshooting

### Common Issues

1. **Elasticsearch not starting:**
   ```bash
   # Check if port 9200 is available
   lsof -i :9200
   
   # Restart Elasticsearch
   docker-compose down
   docker-compose up -d
   ```

2. **Application can't connect to Elasticsearch:**
   ```bash
   # Verify Elasticsearch is accessible
   curl http://localhost:9200/_cluster/health
   
   # Check application logs for connection errors
   mvn spring-boot:run
   ```

3. **No search results:**
   ```bash
   # Check if data is loaded
   curl "http://localhost:9200/courses/_count"
   
   # Refresh the index
   curl -X POST "http://localhost:9200/courses/_refresh"
   ```

4. **Tests failing:**
   ```bash
   # Make sure Docker is running for Testcontainers
   docker ps
   
   # Run tests with more memory
   mvn test -Dspring.elasticsearch.client.reactive.max-in-memory-size=10MB
   ```

## 🎯 Performance Considerations

- **Indexing:** Bulk indexing is used for sample data loading
- **Queries:** Efficient use of filters vs queries for better performance
- **Pagination:** Default page size of 10 to prevent large result sets
- **Caching:** Elasticsearch internal caching for repeated queries

## 🚀 Deployment Notes

For production deployment:

1. **Enable Security:** Configure Elasticsearch security and authentication
2. **Environment Variables:** Use environment-specific configurations
3. **Monitoring:** Add application and Elasticsearch monitoring
4. **Scaling:** Consider multiple Elasticsearch nodes for high availability
5. **Backup:** Implement regular index backups

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📝 License

This project is created for educational purposes as part of an internship assignment.

## 👥 Author

**Shreedhar** - [ShreedharDynamicCraft](https://github.com/ShreedharDynamicCraft)

---

## 📹 Demo Video Checklist

For the required 3-5 minute demo video, make sure to show:

1. ✅ Elasticsearch running locally (`curl http://localhost:9200`)
2. ✅ Spring Boot application starting successfully
3. ✅ Sample data being indexed (check application logs)
4. ✅ `/api/search` endpoint working with:
   - Basic text search
   - Category filtering
   - Age range filtering
   - Price range filtering
   - Pagination
   - Different sorting options
5. ✅ Autocomplete suggestions working (`/api/search/suggest`)
6. ✅ Fuzzy search handling typos
7. ✅ API responses showing correct data structure

**Demo Script Suggestion:**
1. Start with showing Elasticsearch health check
2. Start the Spring Boot application
3. Show data loading logs
4. Demonstrate basic search with results
5. Show filtering capabilities
6. Demonstrate pagination and sorting
7. Show autocomplete suggestions
8. Demonstrate fuzzy search with typos
9. Wrap up showing the total features implemented
